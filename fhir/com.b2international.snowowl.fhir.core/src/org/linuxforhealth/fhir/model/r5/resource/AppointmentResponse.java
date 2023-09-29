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
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ParticipantStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or rejection.
 * 
 * <p>Maturity level: FMM3 (Trial Use)
 */
@Maturity(
    level = 3,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "apr-1",
    level = "Rule",
    location = "(base)",
    description = "Either the participantType or actor must be specified",
    expression = "participantType.exists() or actor.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/AppointmentResponse"
)
@Constraint(
    id = "appointmentResponse-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/encounter-participant-type",
    expression = "participantType.exists() implies (participantType.all(memberOf('http://hl7.org/fhir/ValueSet/encounter-participant-type', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/AppointmentResponse",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AppointmentResponse extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "Appointment" })
    @Required
    private final Reference appointment;
    @Summary
    private final Boolean proposedNewTime;
    private final Instant start;
    private final Instant end;
    @Summary
    @Binding(
        bindingName = "ParticipantType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Role of participant in encounter.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-participant-type"
    )
    private final List<CodeableConcept> participantType;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Practitioner", "PractitionerRole", "RelatedPerson", "Device", "HealthcareService", "Location" })
    private final Reference actor;
    @Summary
    @Binding(
        bindingName = "ParticipantStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The Participation status of an appointment.",
        valueSet = "http://hl7.org/fhir/ValueSet/appointmentresponse-status|5.0.0"
    )
    @Required
    private final ParticipantStatus participantStatus;
    private final Markdown comment;
    private final Boolean recurring;
    private final Date occurrenceDate;
    private final PositiveInt recurrenceId;

    private AppointmentResponse(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        appointment = builder.appointment;
        proposedNewTime = builder.proposedNewTime;
        start = builder.start;
        end = builder.end;
        participantType = Collections.unmodifiableList(builder.participantType);
        actor = builder.actor;
        participantStatus = builder.participantStatus;
        comment = builder.comment;
        recurring = builder.recurring;
        occurrenceDate = builder.occurrenceDate;
        recurrenceId = builder.recurrenceId;
    }

    /**
     * This records identifiers associated with this appointment response concern that are defined by business processes and/ 
     * or used to refer to it when a direct URL reference to the resource itself is not appropriate.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Appointment that this response is replying to.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getAppointment() {
        return appointment;
    }

    /**
     * Indicates that the response is proposing a different time that was initially requested. The new proposed time will be 
     * indicated in the start and end properties.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getProposedNewTime() {
        return proposedNewTime;
    }

    /**
     * Date/Time that the appointment is to take place, or requested new start time.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getStart() {
        return start;
    }

    /**
     * This may be either the same as the appointment request to confirm the details of the appointment, or alternately a new 
     * time to request a re-negotiation of the end time.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getEnd() {
        return end;
    }

    /**
     * Role of participant in the appointment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getParticipantType() {
        return participantType;
    }

    /**
     * A Person, Location, HealthcareService, or Device that is participating in the appointment.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getActor() {
        return actor;
    }

    /**
     * Participation status of the participant. When the status is declined or tentative if the start/end times are different 
     * to the appointment, then these times should be interpreted as a requested time change. When the status is accepted, 
     * the times can either be the time of the appointment (as a confirmation of the time) or can be empty.
     * 
     * @return
     *     An immutable object of type {@link ParticipantStatus} that is non-null.
     */
    public ParticipantStatus getParticipantStatus() {
        return participantStatus;
    }

    /**
     * Additional comments about the appointment.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getComment() {
        return comment;
    }

    /**
     * Indicates that this AppointmentResponse applies to all occurrences in a recurring request.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getRecurring() {
        return recurring;
    }

    /**
     * The original date within a recurring request. This could be used in place of the recurrenceId to be more direct (or 
     * where the template is provided through the simple list of dates in `Appointment.occurrenceDate`).
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getOccurrenceDate() {
        return occurrenceDate;
    }

    /**
     * The recurrence ID (sequence number) of the specific appointment when responding to a recurring request.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getRecurrenceId() {
        return recurrenceId;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (appointment != null) || 
            (proposedNewTime != null) || 
            (start != null) || 
            (end != null) || 
            !participantType.isEmpty() || 
            (actor != null) || 
            (participantStatus != null) || 
            (comment != null) || 
            (recurring != null) || 
            (occurrenceDate != null) || 
            (recurrenceId != null);
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
                accept(appointment, "appointment", visitor);
                accept(proposedNewTime, "proposedNewTime", visitor);
                accept(start, "start", visitor);
                accept(end, "end", visitor);
                accept(participantType, "participantType", visitor, CodeableConcept.class);
                accept(actor, "actor", visitor);
                accept(participantStatus, "participantStatus", visitor);
                accept(comment, "comment", visitor);
                accept(recurring, "recurring", visitor);
                accept(occurrenceDate, "occurrenceDate", visitor);
                accept(recurrenceId, "recurrenceId", visitor);
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
        AppointmentResponse other = (AppointmentResponse) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(appointment, other.appointment) && 
            Objects.equals(proposedNewTime, other.proposedNewTime) && 
            Objects.equals(start, other.start) && 
            Objects.equals(end, other.end) && 
            Objects.equals(participantType, other.participantType) && 
            Objects.equals(actor, other.actor) && 
            Objects.equals(participantStatus, other.participantStatus) && 
            Objects.equals(comment, other.comment) && 
            Objects.equals(recurring, other.recurring) && 
            Objects.equals(occurrenceDate, other.occurrenceDate) && 
            Objects.equals(recurrenceId, other.recurrenceId);
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
                appointment, 
                proposedNewTime, 
                start, 
                end, 
                participantType, 
                actor, 
                participantStatus, 
                comment, 
                recurring, 
                occurrenceDate, 
                recurrenceId);
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
        private Reference appointment;
        private Boolean proposedNewTime;
        private Instant start;
        private Instant end;
        private List<CodeableConcept> participantType = new ArrayList<>();
        private Reference actor;
        private ParticipantStatus participantStatus;
        private Markdown comment;
        private Boolean recurring;
        private Date occurrenceDate;
        private PositiveInt recurrenceId;

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
         * This records identifiers associated with this appointment response concern that are defined by business processes and/ 
         * or used to refer to it when a direct URL reference to the resource itself is not appropriate.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this item
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
         * This records identifiers associated with this appointment response concern that are defined by business processes and/ 
         * or used to refer to it when a direct URL reference to the resource itself is not appropriate.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this item
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
         * Appointment that this response is replying to.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Appointment}</li>
         * </ul>
         * 
         * @param appointment
         *     Appointment this response relates to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder appointment(Reference appointment) {
            this.appointment = appointment;
            return this;
        }

        /**
         * Convenience method for setting {@code proposedNewTime}.
         * 
         * @param proposedNewTime
         *     Indicator for a counter proposal
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #proposedNewTime(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder proposedNewTime(java.lang.Boolean proposedNewTime) {
            this.proposedNewTime = (proposedNewTime == null) ? null : Boolean.of(proposedNewTime);
            return this;
        }

        /**
         * Indicates that the response is proposing a different time that was initially requested. The new proposed time will be 
         * indicated in the start and end properties.
         * 
         * @param proposedNewTime
         *     Indicator for a counter proposal
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder proposedNewTime(Boolean proposedNewTime) {
            this.proposedNewTime = proposedNewTime;
            return this;
        }

        /**
         * Convenience method for setting {@code start}.
         * 
         * @param start
         *     Time from appointment, or requested new start time
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #start(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder start(java.time.ZonedDateTime start) {
            this.start = (start == null) ? null : Instant.of(start);
            return this;
        }

        /**
         * Date/Time that the appointment is to take place, or requested new start time.
         * 
         * @param start
         *     Time from appointment, or requested new start time
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder start(Instant start) {
            this.start = start;
            return this;
        }

        /**
         * Convenience method for setting {@code end}.
         * 
         * @param end
         *     Time from appointment, or requested new end time
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
         * This may be either the same as the appointment request to confirm the details of the appointment, or alternately a new 
         * time to request a re-negotiation of the end time.
         * 
         * @param end
         *     Time from appointment, or requested new end time
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder end(Instant end) {
            this.end = end;
            return this;
        }

        /**
         * Role of participant in the appointment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participantType
         *     Role of participant in the appointment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder participantType(CodeableConcept... participantType) {
            for (CodeableConcept value : participantType) {
                this.participantType.add(value);
            }
            return this;
        }

        /**
         * Role of participant in the appointment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participantType
         *     Role of participant in the appointment
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder participantType(Collection<CodeableConcept> participantType) {
            this.participantType = new ArrayList<>(participantType);
            return this;
        }

        /**
         * A Person, Location, HealthcareService, or Device that is participating in the appointment.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Device}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param actor
         *     Person(s), Location, HealthcareService, or Device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actor(Reference actor) {
            this.actor = actor;
            return this;
        }

        /**
         * Participation status of the participant. When the status is declined or tentative if the start/end times are different 
         * to the appointment, then these times should be interpreted as a requested time change. When the status is accepted, 
         * the times can either be the time of the appointment (as a confirmation of the time) or can be empty.
         * 
         * <p>This element is required.
         * 
         * @param participantStatus
         *     accepted | declined | tentative | needs-action | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder participantStatus(ParticipantStatus participantStatus) {
            this.participantStatus = participantStatus;
            return this;
        }

        /**
         * Additional comments about the appointment.
         * 
         * @param comment
         *     Additional comments
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder comment(Markdown comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Convenience method for setting {@code recurring}.
         * 
         * @param recurring
         *     This response is for all occurrences in a recurring request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #recurring(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder recurring(java.lang.Boolean recurring) {
            this.recurring = (recurring == null) ? null : Boolean.of(recurring);
            return this;
        }

        /**
         * Indicates that this AppointmentResponse applies to all occurrences in a recurring request.
         * 
         * @param recurring
         *     This response is for all occurrences in a recurring request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recurring(Boolean recurring) {
            this.recurring = recurring;
            return this;
        }

        /**
         * Convenience method for setting {@code occurrenceDate}.
         * 
         * @param occurrenceDate
         *     Original date within a recurring request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #occurrenceDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder occurrenceDate(java.time.LocalDate occurrenceDate) {
            this.occurrenceDate = (occurrenceDate == null) ? null : Date.of(occurrenceDate);
            return this;
        }

        /**
         * The original date within a recurring request. This could be used in place of the recurrenceId to be more direct (or 
         * where the template is provided through the simple list of dates in `Appointment.occurrenceDate`).
         * 
         * @param occurrenceDate
         *     Original date within a recurring request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrenceDate(Date occurrenceDate) {
            this.occurrenceDate = occurrenceDate;
            return this;
        }

        /**
         * The recurrence ID (sequence number) of the specific appointment when responding to a recurring request.
         * 
         * @param recurrenceId
         *     The recurrence ID of the specific recurring request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recurrenceId(PositiveInt recurrenceId) {
            this.recurrenceId = recurrenceId;
            return this;
        }

        /**
         * Build the {@link AppointmentResponse}
         * 
         * <p>Required elements:
         * <ul>
         * <li>appointment</li>
         * <li>participantStatus</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link AppointmentResponse}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid AppointmentResponse per the base specification
         */
        @Override
        public AppointmentResponse build() {
            AppointmentResponse appointmentResponse = new AppointmentResponse(this);
            if (validating) {
                validate(appointmentResponse);
            }
            return appointmentResponse;
        }

        protected void validate(AppointmentResponse appointmentResponse) {
            super.validate(appointmentResponse);
            ValidationSupport.checkList(appointmentResponse.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(appointmentResponse.appointment, "appointment");
            ValidationSupport.checkList(appointmentResponse.participantType, "participantType", CodeableConcept.class);
            ValidationSupport.requireNonNull(appointmentResponse.participantStatus, "participantStatus");
            ValidationSupport.checkReferenceType(appointmentResponse.appointment, "appointment", "Appointment");
            ValidationSupport.checkReferenceType(appointmentResponse.actor, "actor", "Patient", "Group", "Practitioner", "PractitionerRole", "RelatedPerson", "Device", "HealthcareService", "Location");
        }

        protected Builder from(AppointmentResponse appointmentResponse) {
            super.from(appointmentResponse);
            identifier.addAll(appointmentResponse.identifier);
            appointment = appointmentResponse.appointment;
            proposedNewTime = appointmentResponse.proposedNewTime;
            start = appointmentResponse.start;
            end = appointmentResponse.end;
            participantType.addAll(appointmentResponse.participantType);
            actor = appointmentResponse.actor;
            participantStatus = appointmentResponse.participantStatus;
            comment = appointmentResponse.comment;
            recurring = appointmentResponse.recurring;
            occurrenceDate = appointmentResponse.occurrenceDate;
            recurrenceId = appointmentResponse.recurrenceId;
            return this;
        }
    }
}
