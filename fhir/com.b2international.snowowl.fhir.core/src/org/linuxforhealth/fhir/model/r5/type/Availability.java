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
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DaysOfWeek;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Availability data for an {item}.
 */
@Constraint(
    id = "av-1",
    level = "Rule",
    location = "Availability.availableTime",
    description = "Cannot include start/end times when selecting all day availability.",
    expression = "allDay.exists().not() or (allDay implies availableStartTime.exists().not() and availableEndTime.exists().not())",
    source = "http://hl7.org/fhir/StructureDefinition/Availability"
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Availability extends DataType {
    @Summary
    private final List<Element> availableTime;
    @Summary
    private final List<Element> notAvailableTime;

    private Availability(Builder builder) {
        super(builder);
        availableTime = Collections.unmodifiableList(builder.availableTime);
        notAvailableTime = Collections.unmodifiableList(builder.notAvailableTime);
    }

    /**
     * Times the {item} is available.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Element} that may be empty.
     */
    public List<Element> getAvailableTime() {
        return availableTime;
    }

    /**
     * Not available during this time due to provided reason.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Element} that may be empty.
     */
    public List<Element> getNotAvailableTime() {
        return notAvailableTime;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !availableTime.isEmpty() || 
            !notAvailableTime.isEmpty();
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(availableTime, "availableTime", visitor, Element.class);
                accept(notAvailableTime, "notAvailableTime", visitor, Element.class);
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
        Availability other = (Availability) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(availableTime, other.availableTime) && 
            Objects.equals(notAvailableTime, other.notAvailableTime);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                availableTime, 
                notAvailableTime);
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
        private List<Element> availableTime = new ArrayList<>();
        private List<Element> notAvailableTime = new ArrayList<>();

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
         * Times the {item} is available.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availableTime
         *     Times the {item} is available
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder availableTime(Element... availableTime) {
            for (Element value : availableTime) {
                this.availableTime.add(value);
            }
            return this;
        }

        /**
         * Times the {item} is available.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availableTime
         *     Times the {item} is available
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder availableTime(Collection<Element> availableTime) {
            this.availableTime = new ArrayList<>(availableTime);
            return this;
        }

        /**
         * Not available during this time due to provided reason.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param notAvailableTime
         *     Not available during this time due to provided reason
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder notAvailableTime(Element... notAvailableTime) {
            for (Element value : notAvailableTime) {
                this.notAvailableTime.add(value);
            }
            return this;
        }

        /**
         * Not available during this time due to provided reason.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param notAvailableTime
         *     Not available during this time due to provided reason
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder notAvailableTime(Collection<Element> notAvailableTime) {
            this.notAvailableTime = new ArrayList<>(notAvailableTime);
            return this;
        }

        /**
         * Build the {@link Availability}
         * 
         * @return
         *     An immutable object of type {@link Availability}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Availability per the base specification
         */
        @Override
        public Availability build() {
            Availability availability = new Availability(this);
            if (validating) {
                validate(availability);
            }
            return availability;
        }

        protected void validate(Availability availability) {
            super.validate(availability);
            ValidationSupport.checkList(availability.availableTime, "availableTime", Element.class);
            ValidationSupport.checkList(availability.notAvailableTime, "notAvailableTime", Element.class);
            ValidationSupport.requireValueOrChildren(availability);
        }

        protected Builder from(Availability availability) {
            super.from(availability);
            availableTime.addAll(availability.availableTime);
            notAvailableTime.addAll(availability.notAvailableTime);
            return this;
        }
    }
}
