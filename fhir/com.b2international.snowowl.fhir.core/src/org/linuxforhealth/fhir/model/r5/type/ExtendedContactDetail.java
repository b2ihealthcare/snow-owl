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
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Specifies contact information for a specific purpose over a period of time, might be handled/monitored by a specific 
 * named person or organization.
 */
@Constraint(
    id = "extendedContactDetail-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/contactentity-type",
    expression = "purpose.exists() implies (purpose.memberOf('http://terminology.hl7.org/ValueSet/contactentity-type', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/ExtendedContactDetail",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ExtendedContactDetail extends DataType {
    @Summary
    @Binding(
        bindingName = "Purpose",
        strength = BindingStrength.Value.PREFERRED,
        description = "The purpose for which an extended contact detail should be used.",
        valueSet = "http://terminology.hl7.org/ValueSet/contactentity-type"
    )
    private final CodeableConcept purpose;
    @Summary
    private final List<HumanName> name;
    @Summary
    private final List<ContactPoint> telecom;
    @Summary
    private final Address address;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference organization;
    @Summary
    private final Period period;

    private ExtendedContactDetail(Builder builder) {
        super(builder);
        purpose = builder.purpose;
        name = Collections.unmodifiableList(builder.name);
        telecom = Collections.unmodifiableList(builder.telecom);
        address = builder.address;
        organization = builder.organization;
        period = builder.period;
    }

    /**
     * The purpose/type of contact.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPurpose() {
        return purpose;
    }

    /**
     * The name of an individual to contact, some types of contact detail are usually blank.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link HumanName} that may be empty.
     */
    public List<HumanName> getName() {
        return name;
    }

    /**
     * The contact details application for the purpose defined.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactPoint} that may be empty.
     */
    public List<ContactPoint> getTelecom() {
        return telecom;
    }

    /**
     * Address for the contact.
     * 
     * @return
     *     An immutable object of type {@link Address} that may be null.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * This contact detail is handled/monitored by a specific organization. If the name is provided in the contact, then it 
     * is referring to the named individual within this organization.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOrganization() {
        return organization;
    }

    /**
     * Period that this contact was valid for usage.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (purpose != null) || 
            !name.isEmpty() || 
            !telecom.isEmpty() || 
            (address != null) || 
            (organization != null) || 
            (period != null);
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(purpose, "purpose", visitor);
                accept(name, "name", visitor, HumanName.class);
                accept(telecom, "telecom", visitor, ContactPoint.class);
                accept(address, "address", visitor);
                accept(organization, "organization", visitor);
                accept(period, "period", visitor);
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
        ExtendedContactDetail other = (ExtendedContactDetail) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(purpose, other.purpose) && 
            Objects.equals(name, other.name) && 
            Objects.equals(telecom, other.telecom) && 
            Objects.equals(address, other.address) && 
            Objects.equals(organization, other.organization) && 
            Objects.equals(period, other.period);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                purpose, 
                name, 
                telecom, 
                address, 
                organization, 
                period);
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
        private CodeableConcept purpose;
        private List<HumanName> name = new ArrayList<>();
        private List<ContactPoint> telecom = new ArrayList<>();
        private Address address;
        private Reference organization;
        private Period period;

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
         * The purpose/type of contact.
         * 
         * @param purpose
         *     The type of contact
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(CodeableConcept purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * The name of an individual to contact, some types of contact detail are usually blank.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     Name of an individual to contact
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(HumanName... name) {
            for (HumanName value : name) {
                this.name.add(value);
            }
            return this;
        }

        /**
         * The name of an individual to contact, some types of contact detail are usually blank.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     Name of an individual to contact
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder name(Collection<HumanName> name) {
            this.name = new ArrayList<>(name);
            return this;
        }

        /**
         * The contact details application for the purpose defined.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param telecom
         *     Contact details (e.g.phone/fax/url)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder telecom(ContactPoint... telecom) {
            for (ContactPoint value : telecom) {
                this.telecom.add(value);
            }
            return this;
        }

        /**
         * The contact details application for the purpose defined.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param telecom
         *     Contact details (e.g.phone/fax/url)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder telecom(Collection<ContactPoint> telecom) {
            this.telecom = new ArrayList<>(telecom);
            return this;
        }

        /**
         * Address for the contact.
         * 
         * @param address
         *     Address for the contact
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        /**
         * This contact detail is handled/monitored by a specific organization. If the name is provided in the contact, then it 
         * is referring to the named individual within this organization.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param organization
         *     This contact detail is handled/monitored by a specific organization
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder organization(Reference organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Period that this contact was valid for usage.
         * 
         * @param period
         *     Period that this contact was valid for usage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Build the {@link ExtendedContactDetail}
         * 
         * @return
         *     An immutable object of type {@link ExtendedContactDetail}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ExtendedContactDetail per the base specification
         */
        @Override
        public ExtendedContactDetail build() {
            ExtendedContactDetail extendedContactDetail = new ExtendedContactDetail(this);
            if (validating) {
                validate(extendedContactDetail);
            }
            return extendedContactDetail;
        }

        protected void validate(ExtendedContactDetail extendedContactDetail) {
            super.validate(extendedContactDetail);
            ValidationSupport.checkList(extendedContactDetail.name, "name", HumanName.class);
            ValidationSupport.checkList(extendedContactDetail.telecom, "telecom", ContactPoint.class);
            ValidationSupport.checkReferenceType(extendedContactDetail.organization, "organization", "Organization");
            ValidationSupport.requireValueOrChildren(extendedContactDetail);
        }

        protected Builder from(ExtendedContactDetail extendedContactDetail) {
            super.from(extendedContactDetail);
            purpose = extendedContactDetail.purpose;
            name.addAll(extendedContactDetail.name);
            telecom.addAll(extendedContactDetail.telecom);
            address = extendedContactDetail.address;
            organization = extendedContactDetail.organization;
            period = extendedContactDetail.period;
            return this;
        }
    }
}
