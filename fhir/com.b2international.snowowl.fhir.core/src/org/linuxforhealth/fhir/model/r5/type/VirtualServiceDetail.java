/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Virtual Service Contact Details.
 */
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class VirtualServiceDetail extends DataType {
    @Summary
    @Binding(
        bindingName = "VirtualServiceType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The purpose for which an extended contact detail should be used.",
        valueSet = "http://hl7.org/fhir/ValueSet/virtual-service-type"
    )
    private final Coding channelType;
    @Summary
    @Choice({ Url.class, String.class, ContactPoint.class, ExtendedContactDetail.class })
    private final Element address;
    @Summary
    private final List<Url> additionalInfo;
    @Summary
    private final PositiveInt maxParticipants;
    @Summary
    private final String sessionKey;

    private VirtualServiceDetail(Builder builder) {
        super(builder);
        channelType = builder.channelType;
        address = builder.address;
        additionalInfo = Collections.unmodifiableList(builder.additionalInfo);
        maxParticipants = builder.maxParticipants;
        sessionKey = builder.sessionKey;
    }

    /**
     * The type of virtual service to connect to (i.e. Teams, Zoom, Specific VMR technology, WhatsApp).
     * 
     * @return
     *     An immutable object of type {@link Coding} that may be null.
     */
    public Coding getChannelType() {
        return channelType;
    }

    /**
     * What address or number needs to be used for a user to connect to the virtual service to join. The channelType informs 
     * as to which datatype is appropriate to use (requires knowledge of the specific type).
     * 
     * @return
     *     An immutable object of type {@link Url}, {@link String}, {@link ContactPoint} or {@link ExtendedContactDetail} that 
     *     may be null.
     */
    public Element getAddress() {
        return address;
    }

    /**
     * Address to see alternative connection details.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Url} that may be empty.
     */
    public List<Url> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Maximum number of participants supported by the virtual service.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Session Key required by the virtual service.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (channelType != null) || 
            (address != null) || 
            !additionalInfo.isEmpty() || 
            (maxParticipants != null) || 
            (sessionKey != null);
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(channelType, "channelType", visitor);
                accept(address, "address", visitor);
                accept(additionalInfo, "additionalInfo", visitor, Url.class);
                accept(maxParticipants, "maxParticipants", visitor);
                accept(sessionKey, "sessionKey", visitor);
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
        VirtualServiceDetail other = (VirtualServiceDetail) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(channelType, other.channelType) && 
            Objects.equals(address, other.address) && 
            Objects.equals(additionalInfo, other.additionalInfo) && 
            Objects.equals(maxParticipants, other.maxParticipants) && 
            Objects.equals(sessionKey, other.sessionKey);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                channelType, 
                address, 
                additionalInfo, 
                maxParticipants, 
                sessionKey);
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

    public static class Builder extends DataType.Builder {
        private Coding channelType;
        private Element address;
        private List<Url> additionalInfo = new ArrayList<>();
        private PositiveInt maxParticipants;
        private String sessionKey;

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
         * The type of virtual service to connect to (i.e. Teams, Zoom, Specific VMR technology, WhatsApp).
         * 
         * @param channelType
         *     Channel Type
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder channelType(Coding channelType) {
            this.channelType = channelType;
            return this;
        }

        /**
         * Convenience method for setting {@code address} with choice type String.
         * 
         * @param address
         *     Contact address/number
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #address(Element)
         */
        public Builder address(java.lang.String address) {
            this.address = (address == null) ? null : String.of(address);
            return this;
        }

        /**
         * What address or number needs to be used for a user to connect to the virtual service to join. The channelType informs 
         * as to which datatype is appropriate to use (requires knowledge of the specific type).
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Url}</li>
         * <li>{@link String}</li>
         * <li>{@link ContactPoint}</li>
         * <li>{@link ExtendedContactDetail}</li>
         * </ul>
         * 
         * @param address
         *     Contact address/number
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder address(Element address) {
            this.address = address;
            return this;
        }

        /**
         * Address to see alternative connection details.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param additionalInfo
         *     Address to see alternative connection details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder additionalInfo(Url... additionalInfo) {
            for (Url value : additionalInfo) {
                this.additionalInfo.add(value);
            }
            return this;
        }

        /**
         * Address to see alternative connection details.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param additionalInfo
         *     Address to see alternative connection details
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder additionalInfo(Collection<Url> additionalInfo) {
            this.additionalInfo = new ArrayList<>(additionalInfo);
            return this;
        }

        /**
         * Maximum number of participants supported by the virtual service.
         * 
         * @param maxParticipants
         *     Maximum number of participants supported by the virtual service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder maxParticipants(PositiveInt maxParticipants) {
            this.maxParticipants = maxParticipants;
            return this;
        }

        /**
         * Convenience method for setting {@code sessionKey}.
         * 
         * @param sessionKey
         *     Session Key required by the virtual service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #sessionKey(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder sessionKey(java.lang.String sessionKey) {
            this.sessionKey = (sessionKey == null) ? null : String.of(sessionKey);
            return this;
        }

        /**
         * Session Key required by the virtual service.
         * 
         * @param sessionKey
         *     Session Key required by the virtual service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder sessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
            return this;
        }

        /**
         * Build the {@link VirtualServiceDetail}
         * 
         * @return
         *     An immutable object of type {@link VirtualServiceDetail}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid VirtualServiceDetail per the base specification
         */
        @Override
        public VirtualServiceDetail build() {
            VirtualServiceDetail virtualServiceDetail = new VirtualServiceDetail(this);
            if (validating) {
                validate(virtualServiceDetail);
            }
            return virtualServiceDetail;
        }

        protected void validate(VirtualServiceDetail virtualServiceDetail) {
            super.validate(virtualServiceDetail);
            ValidationSupport.choiceElement(virtualServiceDetail.address, "address", Url.class, String.class, ContactPoint.class, ExtendedContactDetail.class);
            ValidationSupport.checkList(virtualServiceDetail.additionalInfo, "additionalInfo", Url.class);
            ValidationSupport.requireValueOrChildren(virtualServiceDetail);
        }

        protected Builder from(VirtualServiceDetail virtualServiceDetail) {
            super.from(virtualServiceDetail);
            channelType = virtualServiceDetail.channelType;
            address = virtualServiceDetail.address;
            additionalInfo.addAll(virtualServiceDetail.additionalInfo);
            maxParticipants = virtualServiceDetail.maxParticipants;
            sessionKey = virtualServiceDetail.sessionKey;
            return this;
        }
    }
}
