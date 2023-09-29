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

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.SearchComparator;
import org.linuxforhealth.fhir.model.r5.type.code.SearchModifierCode;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SubscriptionPayloadContent;
import org.linuxforhealth.fhir.model.r5.type.code.SubscriptionStatusCodes;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The subscription resource describes a particular client's request to be notified about a SubscriptionTopic.
 * 
 * <p>Maturity level: FMM3 (Trial Use)
 */
@Maturity(
    level = 3,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "scr-1",
    level = "Rule",
    location = "Subscription.filterBy",
    description = "Subscription filters may only contain a modifier or a comparator",
    expression = "(comparator.exists() and modifier.exists()).not()",
    source = "http://hl7.org/fhir/StructureDefinition/Subscription"
)
@Constraint(
    id = "subscription-2",
    level = "Warning",
    location = "filterBy.resourceType",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/subscription-types",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/subscription-types', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Subscription",
    generated = true
)
@Constraint(
    id = "subscription-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/subscription-channel-type",
    expression = "channelType.exists() and channelType.memberOf('http://hl7.org/fhir/ValueSet/subscription-channel-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Subscription",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Subscription extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final String name;
    @Summary
    @Binding(
        bindingName = "SubscriptionStatusCodes",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of a subscription.",
        valueSet = "http://hl7.org/fhir/ValueSet/subscription-status|5.0.0"
    )
    @Required
    private final SubscriptionStatusCodes status;
    @Summary
    @Required
    private final Canonical topic;
    @Summary
    private final List<ContactPoint> contact;
    @Summary
    private final Instant end;
    @Summary
    @ReferenceTarget({ "CareTeam", "HealthcareService", "Organization", "RelatedPerson", "Patient", "Practitioner", "PractitionerRole" })
    private final Reference managingEntity;
    @Summary
    private final String reason;
    @Summary
    private final List<FilterBy> filterBy;
    @Summary
    @Binding(
        bindingName = "SubscriptionChannelType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The type of method used to execute a subscription.",
        valueSet = "http://hl7.org/fhir/ValueSet/subscription-channel-type"
    )
    @Required
    private final Coding channelType;
    @Summary
    private final Url endpoint;
    private final List<Parameter> parameter;
    @Summary
    private final UnsignedInt heartbeatPeriod;
    @Summary
    private final UnsignedInt timeout;
    @Summary
    @Binding(
        bindingName = "MimeType",
        strength = BindingStrength.Value.REQUIRED,
        description = "BCP 13 (RFCs 2045, 2046, 2047, 4288, 4289 and 2049)",
        valueSet = "http://hl7.org/fhir/ValueSet/mimetypes|5.0.0"
    )
    private final Code contentType;
    @Summary
    @Binding(
        bindingName = "SubscriptionPayloadContent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes to represent how much resource content to send in the notification payload.",
        valueSet = "http://hl7.org/fhir/ValueSet/subscription-payload-content|5.0.0"
    )
    private final SubscriptionPayloadContent content;
    @Summary
    private final PositiveInt maxCount;

    private Subscription(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        name = builder.name;
        status = builder.status;
        topic = builder.topic;
        contact = Collections.unmodifiableList(builder.contact);
        end = builder.end;
        managingEntity = builder.managingEntity;
        reason = builder.reason;
        filterBy = Collections.unmodifiableList(builder.filterBy);
        channelType = builder.channelType;
        endpoint = builder.endpoint;
        parameter = Collections.unmodifiableList(builder.parameter);
        heartbeatPeriod = builder.heartbeatPeriod;
        timeout = builder.timeout;
        contentType = builder.contentType;
        content = builder.content;
        maxCount = builder.maxCount;
    }

    /**
     * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
     * in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A natural language name identifying the subscription.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * The status of the subscription, which marks the server state for managing the subscription.
     * 
     * @return
     *     An immutable object of type {@link SubscriptionStatusCodes} that is non-null.
     */
    public SubscriptionStatusCodes getStatus() {
        return status;
    }

    /**
     * The reference to the subscription topic to be notified about.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that is non-null.
     */
    public Canonical getTopic() {
        return topic;
    }

    /**
     * Contact details for a human to contact about the subscription. The primary use of this for system administrator 
     * troubleshooting.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactPoint} that may be empty.
     */
    public List<ContactPoint> getContact() {
        return contact;
    }

    /**
     * The time for the server to turn the subscription off.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getEnd() {
        return end;
    }

    /**
     * Entity with authorization to make subsequent revisions to the Subscription and also determines what data the 
     * subscription is authorized to disclose.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getManagingEntity() {
        return managingEntity;
    }

    /**
     * A description of why this subscription is defined.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getReason() {
        return reason;
    }

    /**
     * The filter properties to be applied to narrow the subscription topic stream. When multiple filters are applied, 
     * evaluates to true if all the conditions applicable to that resource are met; otherwise it returns false (i.e., logical 
     * AND).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link FilterBy} that may be empty.
     */
    public List<FilterBy> getFilterBy() {
        return filterBy;
    }

    /**
     * The type of channel to send notifications on.
     * 
     * @return
     *     An immutable object of type {@link Coding} that is non-null.
     */
    public Coding getChannelType() {
        return channelType;
    }

    /**
     * The url that describes the actual end-point to send notifications to.
     * 
     * @return
     *     An immutable object of type {@link Url} that may be null.
     */
    public Url getEndpoint() {
        return endpoint;
    }

    /**
     * Channel-dependent information to send as part of the notification (e.g., HTTP Headers).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Parameter} that may be empty.
     */
    public List<Parameter> getParameter() {
        return parameter;
    }

    /**
     * If present, a 'heartbeat' notification (keep-alive) is sent via this channel with an interval period equal to this 
     * elements integer value in seconds. If not present, a heartbeat notification is not sent.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    /**
     * If present, the maximum amount of time a server will allow before failing a notification attempt.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getTimeout() {
        return timeout;
    }

    /**
     * The MIME type to send the payload in - e.g., `application/fhir+xml` or `application/fhir+json`. Note that:

* clients 
     * may request notifications in a specific FHIR version by using the [FHIR Version Parameter](http.html#version-
     * parameter) - e.g., `application/fhir+json; fhirVersion=4.0`.

* additional MIME types can be allowed by channels - e.
     * g., `text/plain` and `text/html` are defined by the Email channel.
     * 
     * @return
     *     An immutable object of type {@link Code} that may be null.
     */
    public Code getContentType() {
        return contentType;
    }

    /**
     * How much of the resource content to deliver in the notification payload. The choices are an empty payload, only the 
     * resource id, or the full resource content.
     * 
     * @return
     *     An immutable object of type {@link SubscriptionPayloadContent} that may be null.
     */
    public SubscriptionPayloadContent getContent() {
        return content;
    }

    /**
     * If present, the maximum number of events that will be included in a notification bundle. Note that this is not a 
     * strict limit on the number of entries in a bundle, as dependent resources can be included.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getMaxCount() {
        return maxCount;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (name != null) || 
            (status != null) || 
            (topic != null) || 
            !contact.isEmpty() || 
            (end != null) || 
            (managingEntity != null) || 
            (reason != null) || 
            !filterBy.isEmpty() || 
            (channelType != null) || 
            (endpoint != null) || 
            !parameter.isEmpty() || 
            (heartbeatPeriod != null) || 
            (timeout != null) || 
            (contentType != null) || 
            (content != null) || 
            (maxCount != null);
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
                accept(text, "text", visitor);
                accept(contained, "contained", visitor, Resource.class);
                accept(extension, "extension", visitor, Extension.class);
                accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(name, "name", visitor);
                accept(status, "status", visitor);
                accept(topic, "topic", visitor);
                accept(contact, "contact", visitor, ContactPoint.class);
                accept(end, "end", visitor);
                accept(managingEntity, "managingEntity", visitor);
                accept(reason, "reason", visitor);
                accept(filterBy, "filterBy", visitor, FilterBy.class);
                accept(channelType, "channelType", visitor);
                accept(endpoint, "endpoint", visitor);
                accept(parameter, "parameter", visitor, Parameter.class);
                accept(heartbeatPeriod, "heartbeatPeriod", visitor);
                accept(timeout, "timeout", visitor);
                accept(contentType, "contentType", visitor);
                accept(content, "content", visitor);
                accept(maxCount, "maxCount", visitor);
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
        Subscription other = (Subscription) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(name, other.name) && 
            Objects.equals(status, other.status) && 
            Objects.equals(topic, other.topic) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(end, other.end) && 
            Objects.equals(managingEntity, other.managingEntity) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(filterBy, other.filterBy) && 
            Objects.equals(channelType, other.channelType) && 
            Objects.equals(endpoint, other.endpoint) && 
            Objects.equals(parameter, other.parameter) && 
            Objects.equals(heartbeatPeriod, other.heartbeatPeriod) && 
            Objects.equals(timeout, other.timeout) && 
            Objects.equals(contentType, other.contentType) && 
            Objects.equals(content, other.content) && 
            Objects.equals(maxCount, other.maxCount);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                meta, 
                implicitRules, 
                language, 
                text, 
                contained, 
                extension, 
                modifierExtension, 
                identifier, 
                name, 
                status, 
                topic, 
                contact, 
                end, 
                managingEntity, 
                reason, 
                filterBy, 
                channelType, 
                endpoint, 
                parameter, 
                heartbeatPeriod, 
                timeout, 
                contentType, 
                content, 
                maxCount);
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

    public static class Builder extends DomainResource.Builder {
        private List<Identifier> identifier = new ArrayList<>();
        private String name;
        private SubscriptionStatusCodes status;
        private Canonical topic;
        private List<ContactPoint> contact = new ArrayList<>();
        private Instant end;
        private Reference managingEntity;
        private String reason;
        private List<FilterBy> filterBy = new ArrayList<>();
        private Coding channelType;
        private Url endpoint;
        private List<Parameter> parameter = new ArrayList<>();
        private UnsignedInt heartbeatPeriod;
        private UnsignedInt timeout;
        private Code contentType;
        private SubscriptionPayloadContent content;
        private PositiveInt maxCount;

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
         * A human-readable narrative that contains a summary of the resource and can be used to represent the content of the 
         * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
         * detail to make it "clinically safe" for a human to just read the narrative. Resource definitions may define what 
         * content should be represented in the narrative to ensure clinical safety.
         * 
         * @param text
         *     Text summary of the resource, for human interpretation
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder text(Narrative text) {
            return (Builder) super.text(text);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder contained(Resource... contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder contained(Collection<Resource> contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
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
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
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
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder modifierExtension(Extension... modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
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
         * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifiers (business identifier)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier... identifier) {
            for (Identifier value : identifier) {
                this.identifier.add(value);
            }
            return this;
        }

        /**
         * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifiers (business identifier)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder identifier(Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Human readable name for this subscription
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
         * A natural language name identifying the subscription.
         * 
         * @param name
         *     Human readable name for this subscription
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * The status of the subscription, which marks the server state for managing the subscription.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     requested | active | error | off | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(SubscriptionStatusCodes status) {
            this.status = status;
            return this;
        }

        /**
         * The reference to the subscription topic to be notified about.
         * 
         * <p>This element is required.
         * 
         * @param topic
         *     Reference to the subscription topic being subscribed to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder topic(Canonical topic) {
            this.topic = topic;
            return this;
        }

        /**
         * Contact details for a human to contact about the subscription. The primary use of this for system administrator 
         * troubleshooting.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for source (e.g. troubleshooting)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contact(ContactPoint... contact) {
            for (ContactPoint value : contact) {
                this.contact.add(value);
            }
            return this;
        }

        /**
         * Contact details for a human to contact about the subscription. The primary use of this for system administrator 
         * troubleshooting.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for source (e.g. troubleshooting)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contact(Collection<ContactPoint> contact) {
            this.contact = new ArrayList<>(contact);
            return this;
        }

        /**
         * Convenience method for setting {@code end}.
         * 
         * @param end
         *     When to automatically delete the subscription
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #end(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder end(java.time.ZonedDateTime end) {
            this.end = (end == null) ? null : Instant.of(end);
            return this;
        }

        /**
         * The time for the server to turn the subscription off.
         * 
         * @param end
         *     When to automatically delete the subscription
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder end(Instant end) {
            this.end = end;
            return this;
        }

        /**
         * Entity with authorization to make subsequent revisions to the Subscription and also determines what data the 
         * subscription is authorized to disclose.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param managingEntity
         *     Entity responsible for Subscription changes
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder managingEntity(Reference managingEntity) {
            this.managingEntity = managingEntity;
            return this;
        }

        /**
         * Convenience method for setting {@code reason}.
         * 
         * @param reason
         *     Description of why this subscription was created
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #reason(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder reason(java.lang.String reason) {
            this.reason = (reason == null) ? null : String.of(reason);
            return this;
        }

        /**
         * A description of why this subscription is defined.
         * 
         * @param reason
         *     Description of why this subscription was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        /**
         * The filter properties to be applied to narrow the subscription topic stream. When multiple filters are applied, 
         * evaluates to true if all the conditions applicable to that resource are met; otherwise it returns false (i.e., logical 
         * AND).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param filterBy
         *     Criteria for narrowing the subscription topic stream
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder filterBy(FilterBy... filterBy) {
            for (FilterBy value : filterBy) {
                this.filterBy.add(value);
            }
            return this;
        }

        /**
         * The filter properties to be applied to narrow the subscription topic stream. When multiple filters are applied, 
         * evaluates to true if all the conditions applicable to that resource are met; otherwise it returns false (i.e., logical 
         * AND).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param filterBy
         *     Criteria for narrowing the subscription topic stream
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder filterBy(Collection<FilterBy> filterBy) {
            this.filterBy = new ArrayList<>(filterBy);
            return this;
        }

        /**
         * The type of channel to send notifications on.
         * 
         * <p>This element is required.
         * 
         * @param channelType
         *     Channel type for notifications
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder channelType(Coding channelType) {
            this.channelType = channelType;
            return this;
        }

        /**
         * The url that describes the actual end-point to send notifications to.
         * 
         * @param endpoint
         *     Where the channel points to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder endpoint(Url endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Channel-dependent information to send as part of the notification (e.g., HTTP Headers).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Channel type
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
         * Channel-dependent information to send as part of the notification (e.g., HTTP Headers).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Channel type
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
         * If present, a 'heartbeat' notification (keep-alive) is sent via this channel with an interval period equal to this 
         * elements integer value in seconds. If not present, a heartbeat notification is not sent.
         * 
         * @param heartbeatPeriod
         *     Interval in seconds to send 'heartbeat' notification
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder heartbeatPeriod(UnsignedInt heartbeatPeriod) {
            this.heartbeatPeriod = heartbeatPeriod;
            return this;
        }

        /**
         * If present, the maximum amount of time a server will allow before failing a notification attempt.
         * 
         * @param timeout
         *     Timeout in seconds to attempt notification delivery
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder timeout(UnsignedInt timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * The MIME type to send the payload in - e.g., `application/fhir+xml` or `application/fhir+json`. Note that:

* clients 
         * may request notifications in a specific FHIR version by using the [FHIR Version Parameter](http.html#version-
         * parameter) - e.g., `application/fhir+json; fhirVersion=4.0`.

* additional MIME types can be allowed by channels - e.
         * g., `text/plain` and `text/html` are defined by the Email channel.
         * 
         * @param contentType
         *     MIME type to send, or omit for no payload
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contentType(Code contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * How much of the resource content to deliver in the notification payload. The choices are an empty payload, only the 
         * resource id, or the full resource content.
         * 
         * @param content
         *     empty | id-only | full-resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder content(SubscriptionPayloadContent content) {
            this.content = content;
            return this;
        }

        /**
         * If present, the maximum number of events that will be included in a notification bundle. Note that this is not a 
         * strict limit on the number of entries in a bundle, as dependent resources can be included.
         * 
         * @param maxCount
         *     Maximum number of events that can be combined in a single notification
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder maxCount(PositiveInt maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        /**
         * Build the {@link Subscription}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>topic</li>
         * <li>channelType</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Subscription}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Subscription per the base specification
         */
        @Override
        public Subscription build() {
            Subscription subscription = new Subscription(this);
            if (validating) {
                validate(subscription);
            }
            return subscription;
        }

        protected void validate(Subscription subscription) {
            super.validate(subscription);
            ValidationSupport.checkList(subscription.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(subscription.status, "status");
            ValidationSupport.requireNonNull(subscription.topic, "topic");
            ValidationSupport.checkList(subscription.contact, "contact", ContactPoint.class);
            ValidationSupport.checkList(subscription.filterBy, "filterBy", FilterBy.class);
            ValidationSupport.requireNonNull(subscription.channelType, "channelType");
            ValidationSupport.checkList(subscription.parameter, "parameter", Parameter.class);
            ValidationSupport.checkReferenceType(subscription.managingEntity, "managingEntity", "CareTeam", "HealthcareService", "Organization", "RelatedPerson", "Patient", "Practitioner", "PractitionerRole");
        }

        protected Builder from(Subscription subscription) {
            super.from(subscription);
            identifier.addAll(subscription.identifier);
            name = subscription.name;
            status = subscription.status;
            topic = subscription.topic;
            contact.addAll(subscription.contact);
            end = subscription.end;
            managingEntity = subscription.managingEntity;
            reason = subscription.reason;
            filterBy.addAll(subscription.filterBy);
            channelType = subscription.channelType;
            endpoint = subscription.endpoint;
            parameter.addAll(subscription.parameter);
            heartbeatPeriod = subscription.heartbeatPeriod;
            timeout = subscription.timeout;
            contentType = subscription.contentType;
            content = subscription.content;
            maxCount = subscription.maxCount;
            return this;
        }
    }

    /**
     * The filter properties to be applied to narrow the subscription topic stream. When multiple filters are applied, 
     * evaluates to true if all the conditions applicable to that resource are met; otherwise it returns false (i.e., logical 
     * AND).
     */
    public static class FilterBy extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "FHIRTypes",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "A type of resource, or a Reference (from all versions)",
            valueSet = "http://hl7.org/fhir/ValueSet/subscription-types"
        )
        private final Uri resourceType;
        @Summary
        @Required
        private final String filterParameter;
        @Binding(
            bindingName = "SearchComparator",
            strength = BindingStrength.Value.REQUIRED,
            description = "Search Comparator Codes applied to this filter.",
            valueSet = "http://hl7.org/fhir/ValueSet/search-comparator|5.0.0"
        )
        private final SearchComparator comparator;
        @Binding(
            bindingName = "SearchModifierCode",
            strength = BindingStrength.Value.REQUIRED,
            description = "Search Modifier Code applied to this filter.",
            valueSet = "http://hl7.org/fhir/ValueSet/search-modifier-code|5.0.0"
        )
        private final SearchModifierCode modifier;
        @Summary
        @Required
        private final String value;

        private FilterBy(Builder builder) {
            super(builder);
            resourceType = builder.resourceType;
            filterParameter = builder.filterParameter;
            comparator = builder.comparator;
            modifier = builder.modifier;
            value = builder.value;
        }

        /**
         * A resource listed in the `SubscriptionTopic` this `Subscription` references (`SubscriptionTopic.canFilterBy.
         * resource`). This element can be used to differentiate filters for topics that include more than one resource type.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getResourceType() {
            return resourceType;
        }

        /**
         * The filter as defined in the `SubscriptionTopic.canFilterBy.filterParameter` element.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getFilterParameter() {
            return filterParameter;
        }

        /**
         * Comparator applied to this filter parameter.
         * 
         * @return
         *     An immutable object of type {@link SearchComparator} that may be null.
         */
        public SearchComparator getComparator() {
            return comparator;
        }

        /**
         * Modifier applied to this filter parameter.
         * 
         * @return
         *     An immutable object of type {@link SearchModifierCode} that may be null.
         */
        public SearchModifierCode getModifier() {
            return modifier;
        }

        /**
         * The literal value or resource path as is legal in search - for example, `Patient/123` or `le1950`.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (resourceType != null) || 
                (filterParameter != null) || 
                (comparator != null) || 
                (modifier != null) || 
                (value != null);
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
                    accept(resourceType, "resourceType", visitor);
                    accept(filterParameter, "filterParameter", visitor);
                    accept(comparator, "comparator", visitor);
                    accept(modifier, "modifier", visitor);
                    accept(value, "value", visitor);
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
            FilterBy other = (FilterBy) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(resourceType, other.resourceType) && 
                Objects.equals(filterParameter, other.filterParameter) && 
                Objects.equals(comparator, other.comparator) && 
                Objects.equals(modifier, other.modifier) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    resourceType, 
                    filterParameter, 
                    comparator, 
                    modifier, 
                    value);
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
            private Uri resourceType;
            private String filterParameter;
            private SearchComparator comparator;
            private SearchModifierCode modifier;
            private String value;

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
             * A resource listed in the `SubscriptionTopic` this `Subscription` references (`SubscriptionTopic.canFilterBy.
             * resource`). This element can be used to differentiate filters for topics that include more than one resource type.
             * 
             * @param resourceType
             *     Allowed Resource (reference to definition) for this Subscription filter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder resourceType(Uri resourceType) {
                this.resourceType = resourceType;
                return this;
            }

            /**
             * Convenience method for setting {@code filterParameter}.
             * 
             * <p>This element is required.
             * 
             * @param filterParameter
             *     Filter label defined in SubscriptionTopic
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #filterParameter(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder filterParameter(java.lang.String filterParameter) {
                this.filterParameter = (filterParameter == null) ? null : String.of(filterParameter);
                return this;
            }

            /**
             * The filter as defined in the `SubscriptionTopic.canFilterBy.filterParameter` element.
             * 
             * <p>This element is required.
             * 
             * @param filterParameter
             *     Filter label defined in SubscriptionTopic
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder filterParameter(String filterParameter) {
                this.filterParameter = filterParameter;
                return this;
            }

            /**
             * Comparator applied to this filter parameter.
             * 
             * @param comparator
             *     eq | ne | gt | lt | ge | le | sa | eb | ap
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder comparator(SearchComparator comparator) {
                this.comparator = comparator;
                return this;
            }

            /**
             * Modifier applied to this filter parameter.
             * 
             * @param modifier
             *     missing | exact | contains | not | text | in | not-in | below | above | type | identifier | of-type | code-text | text-
             *     advanced | iterate
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder modifier(SearchModifierCode modifier) {
                this.modifier = modifier;
                return this;
            }

            /**
             * Convenience method for setting {@code value}.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Literal value or resource path
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * The literal value or resource path as is legal in search - for example, `Patient/123` or `le1950`.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Literal value or resource path
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link FilterBy}
             * 
             * <p>Required elements:
             * <ul>
             * <li>filterParameter</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link FilterBy}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid FilterBy per the base specification
             */
            @Override
            public FilterBy build() {
                FilterBy filterBy = new FilterBy(this);
                if (validating) {
                    validate(filterBy);
                }
                return filterBy;
            }

            protected void validate(FilterBy filterBy) {
                super.validate(filterBy);
                ValidationSupport.requireNonNull(filterBy.filterParameter, "filterParameter");
                ValidationSupport.requireNonNull(filterBy.value, "value");
                ValidationSupport.requireValueOrChildren(filterBy);
            }

            protected Builder from(FilterBy filterBy) {
                super.from(filterBy);
                resourceType = filterBy.resourceType;
                filterParameter = filterBy.filterParameter;
                comparator = filterBy.comparator;
                modifier = filterBy.modifier;
                value = filterBy.value;
                return this;
            }
        }
    }

    /**
     * Channel-dependent information to send as part of the notification (e.g., HTTP Headers).
     */
    public static class Parameter extends BackboneElement {
        @Required
        private final String name;
        @Required
        private final String value;

        private Parameter(Builder builder) {
            super(builder);
            name = builder.name;
            value = builder.value;
        }

        /**
         * Parameter name for information passed to the channel for notifications, for example in the case of a REST hook wanting 
         * to pass through an authorization header, the name would be Authorization.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getName() {
            return name;
        }

        /**
         * Parameter value for information passed to the channel for notifications, for example in the case of a REST hook 
         * wanting to pass through an authorization header, the value would be `Bearer 0193...`.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                (value != null);
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
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    value);
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
            private String value;

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
             *     Name (key) of the parameter
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
             * Parameter name for information passed to the channel for notifications, for example in the case of a REST hook wanting 
             * to pass through an authorization header, the name would be Authorization.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     Name (key) of the parameter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Convenience method for setting {@code value}.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value of the parameter to use or pass through
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * Parameter value for information passed to the channel for notifications, for example in the case of a REST hook 
             * wanting to pass through an authorization header, the value would be `Bearer 0193...`.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value of the parameter to use or pass through
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Parameter}
             * 
             * <p>Required elements:
             * <ul>
             * <li>name</li>
             * <li>value</li>
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
                ValidationSupport.requireNonNull(parameter.value, "value");
                ValidationSupport.requireValueOrChildren(parameter);
            }

            protected Builder from(Parameter parameter) {
                super.from(parameter);
                name = parameter.name;
                value = parameter.value;
                return this;
            }
        }
    }
}
