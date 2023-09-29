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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceMetricCalibrationState;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceMetricCalibrationType;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceMetricCategory;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceMetricOperationalStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Describes a measurement, calculation or setting capability of a device.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "deviceMetric-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/devicemetric-type",
    expression = "type.exists() and type.memberOf('http://hl7.org/fhir/ValueSet/devicemetric-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/DeviceMetric",
    generated = true
)
@Constraint(
    id = "deviceMetric-1",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/ucum-units",
    expression = "unit.exists() implies (unit.memberOf('http://hl7.org/fhir/ValueSet/ucum-units', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/DeviceMetric",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceMetric extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "MetricType",
        strength = BindingStrength.Value.PREFERRED,
        description = "IEEE 11073-10101",
        valueSet = "http://hl7.org/fhir/ValueSet/devicemetric-type"
    )
    @Required
    private final CodeableConcept type;
    @Summary
    @Binding(
        bindingName = "MetricUnit",
        strength = BindingStrength.Value.PREFERRED,
        description = "IEEE 11073-10101",
        valueSet = "http://hl7.org/fhir/ValueSet/ucum-units"
    )
    private final CodeableConcept unit;
    @Summary
    @ReferenceTarget({ "Device" })
    @Required
    private final Reference device;
    @Summary
    @Binding(
        bindingName = "DeviceMetricOperationalStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the operational status of the DeviceMetric.",
        valueSet = "http://hl7.org/fhir/ValueSet/metric-operational-status|5.0.0"
    )
    private final DeviceMetricOperationalStatus operationalStatus;
    @Binding(
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the typical color of representation.",
        valueSet = "http://hl7.org/fhir/ValueSet/color-codes|5.0.0"
    )
    private final org.linuxforhealth.fhir.model.r5.type.Code color;
    @Summary
    @Binding(
        bindingName = "DeviceMetricCategory",
        strength = BindingStrength.Value.REQUIRED,
        description = "Describes the category of the metric.",
        valueSet = "http://hl7.org/fhir/ValueSet/metric-category|5.0.0"
    )
    @Required
    private final DeviceMetricCategory category;
    private final Quantity measurementFrequency;
    private final List<Calibration> calibration;

    private DeviceMetric(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        type = builder.type;
        unit = builder.unit;
        device = builder.device;
        operationalStatus = builder.operationalStatus;
        color = builder.color;
        category = builder.category;
        measurementFrequency = builder.measurementFrequency;
        calibration = Collections.unmodifiableList(builder.calibration);
    }

    /**
     * Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or 
     * owners. For example, handle ID.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getUnit() {
        return unit;
    }

    /**
     * Describes the link to the Device. This is also known as a channel device.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getDevice() {
        return device;
    }

    /**
     * Indicates current operational state of the device. For example: On, Off, Standby, etc.
     * 
     * @return
     *     An immutable object of type {@link DeviceMetricOperationalStatus} that may be null.
     */
    public DeviceMetricOperationalStatus getOperationalStatus() {
        return operationalStatus;
    }

    /**
     * The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track 
     * and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics 
     * are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.
     * 
     * @return
     *     An immutable object of type {@link org.linuxforhealth.fhir.model.r5.type.Code} that may be null.
     */
    public org.linuxforhealth.fhir.model.r5.type.Code getColor() {
        return color;
    }

    /**
     * Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, 
     * measurement, or calculation.
     * 
     * @return
     *     An immutable object of type {@link DeviceMetricCategory} that is non-null.
     */
    public DeviceMetricCategory getCategory() {
        return category;
    }

    /**
     * The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for 
     * example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. 
     * Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring 
     * wheel). The update period may be different than the measurement frequency, if the device does not update the published 
     * observed value with the same frequency as it was measured.
     * 
     * @return
     *     An immutable object of type {@link Quantity} that may be null.
     */
    public Quantity getMeasurementFrequency() {
        return measurementFrequency;
    }

    /**
     * Describes the calibrations that have been performed or that are required to be performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Calibration} that may be empty.
     */
    public List<Calibration> getCalibration() {
        return calibration;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (type != null) || 
            (unit != null) || 
            (device != null) || 
            (operationalStatus != null) || 
            (color != null) || 
            (category != null) || 
            (measurementFrequency != null) || 
            !calibration.isEmpty();
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
                accept(type, "type", visitor);
                accept(unit, "unit", visitor);
                accept(device, "device", visitor);
                accept(operationalStatus, "operationalStatus", visitor);
                accept(color, "color", visitor);
                accept(category, "category", visitor);
                accept(measurementFrequency, "measurementFrequency", visitor);
                accept(calibration, "calibration", visitor, Calibration.class);
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
        DeviceMetric other = (DeviceMetric) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(type, other.type) && 
            Objects.equals(unit, other.unit) && 
            Objects.equals(device, other.device) && 
            Objects.equals(operationalStatus, other.operationalStatus) && 
            Objects.equals(color, other.color) && 
            Objects.equals(category, other.category) && 
            Objects.equals(measurementFrequency, other.measurementFrequency) && 
            Objects.equals(calibration, other.calibration);
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
                type, 
                unit, 
                device, 
                operationalStatus, 
                color, 
                category, 
                measurementFrequency, 
                calibration);
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
        private CodeableConcept type;
        private CodeableConcept unit;
        private Reference device;
        private DeviceMetricOperationalStatus operationalStatus;
        private org.linuxforhealth.fhir.model.r5.type.Code color;
        private DeviceMetricCategory category;
        private Quantity measurementFrequency;
        private List<Calibration> calibration = new ArrayList<>();

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
         * Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or 
         * owners. For example, handle ID.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or 
         * owners. For example, handle ID.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     Identity of metric, for example Heart Rate or PEEP Setting
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.
         * 
         * @param unit
         *     Unit of Measure for the Metric
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder unit(CodeableConcept unit) {
            this.unit = unit;
            return this;
        }

        /**
         * Describes the link to the Device. This is also known as a channel device.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param device
         *     Describes the link to the Device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(Reference device) {
            this.device = device;
            return this;
        }

        /**
         * Indicates current operational state of the device. For example: On, Off, Standby, etc.
         * 
         * @param operationalStatus
         *     on | off | standby | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder operationalStatus(DeviceMetricOperationalStatus operationalStatus) {
            this.operationalStatus = operationalStatus;
            return this;
        }

        /**
         * The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track 
         * and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics 
         * are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.
         * 
         * @param color
         *     Color name (from CSS4) or #RRGGBB code
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder color(org.linuxforhealth.fhir.model.r5.type.Code color) {
            this.color = color;
            return this;
        }

        /**
         * Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, 
         * measurement, or calculation.
         * 
         * <p>This element is required.
         * 
         * @param category
         *     measurement | setting | calculation | unspecified
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(DeviceMetricCategory category) {
            this.category = category;
            return this;
        }

        /**
         * The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for 
         * example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. 
         * Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring 
         * wheel). The update period may be different than the measurement frequency, if the device does not update the published 
         * observed value with the same frequency as it was measured.
         * 
         * @param measurementFrequency
         *     Indicates how often the metric is taken or recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder measurementFrequency(Quantity measurementFrequency) {
            this.measurementFrequency = measurementFrequency;
            return this;
        }

        /**
         * Describes the calibrations that have been performed or that are required to be performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param calibration
         *     Describes the calibrations that have been performed or that are required to be performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder calibration(Calibration... calibration) {
            for (Calibration value : calibration) {
                this.calibration.add(value);
            }
            return this;
        }

        /**
         * Describes the calibrations that have been performed or that are required to be performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param calibration
         *     Describes the calibrations that have been performed or that are required to be performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder calibration(Collection<Calibration> calibration) {
            this.calibration = new ArrayList<>(calibration);
            return this;
        }

        /**
         * Build the {@link DeviceMetric}
         * 
         * <p>Required elements:
         * <ul>
         * <li>type</li>
         * <li>device</li>
         * <li>category</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DeviceMetric}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceMetric per the base specification
         */
        @Override
        public DeviceMetric build() {
            DeviceMetric deviceMetric = new DeviceMetric(this);
            if (validating) {
                validate(deviceMetric);
            }
            return deviceMetric;
        }

        protected void validate(DeviceMetric deviceMetric) {
            super.validate(deviceMetric);
            ValidationSupport.checkList(deviceMetric.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(deviceMetric.type, "type");
            ValidationSupport.requireNonNull(deviceMetric.device, "device");
            ValidationSupport.requireNonNull(deviceMetric.category, "category");
            ValidationSupport.checkList(deviceMetric.calibration, "calibration", Calibration.class);
            ValidationSupport.checkReferenceType(deviceMetric.device, "device", "Device");
        }

        protected Builder from(DeviceMetric deviceMetric) {
            super.from(deviceMetric);
            identifier.addAll(deviceMetric.identifier);
            type = deviceMetric.type;
            unit = deviceMetric.unit;
            device = deviceMetric.device;
            operationalStatus = deviceMetric.operationalStatus;
            color = deviceMetric.color;
            category = deviceMetric.category;
            measurementFrequency = deviceMetric.measurementFrequency;
            calibration.addAll(deviceMetric.calibration);
            return this;
        }
    }

    /**
     * Describes the calibrations that have been performed or that are required to be performed.
     */
    public static class Calibration extends BackboneElement {
        @Binding(
            bindingName = "DeviceMetricCalibrationType",
            strength = BindingStrength.Value.REQUIRED,
            description = "Describes the type of a metric calibration.",
            valueSet = "http://hl7.org/fhir/ValueSet/metric-calibration-type|5.0.0"
        )
        private final DeviceMetricCalibrationType type;
        @Binding(
            bindingName = "DeviceMetricCalibrationState",
            strength = BindingStrength.Value.REQUIRED,
            description = "Describes the state of a metric calibration.",
            valueSet = "http://hl7.org/fhir/ValueSet/metric-calibration-state|5.0.0"
        )
        private final DeviceMetricCalibrationState state;
        private final Instant time;

        private Calibration(Builder builder) {
            super(builder);
            type = builder.type;
            state = builder.state;
            time = builder.time;
        }

        /**
         * Describes the type of the calibration method.
         * 
         * @return
         *     An immutable object of type {@link DeviceMetricCalibrationType} that may be null.
         */
        public DeviceMetricCalibrationType getType() {
            return type;
        }

        /**
         * Describes the state of the calibration.
         * 
         * @return
         *     An immutable object of type {@link DeviceMetricCalibrationState} that may be null.
         */
        public DeviceMetricCalibrationState getState() {
            return state;
        }

        /**
         * Describes the time last calibration has been performed.
         * 
         * @return
         *     An immutable object of type {@link Instant} that may be null.
         */
        public Instant getTime() {
            return time;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (state != null) || 
                (time != null);
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
                    accept(type, "type", visitor);
                    accept(state, "state", visitor);
                    accept(time, "time", visitor);
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
            Calibration other = (Calibration) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(state, other.state) && 
                Objects.equals(time, other.time);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    state, 
                    time);
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
            private DeviceMetricCalibrationType type;
            private DeviceMetricCalibrationState state;
            private Instant time;

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
             * Describes the type of the calibration method.
             * 
             * @param type
             *     unspecified | offset | gain | two-point
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(DeviceMetricCalibrationType type) {
                this.type = type;
                return this;
            }

            /**
             * Describes the state of the calibration.
             * 
             * @param state
             *     not-calibrated | calibration-required | calibrated | unspecified
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder state(DeviceMetricCalibrationState state) {
                this.state = state;
                return this;
            }

            /**
             * Convenience method for setting {@code time}.
             * 
             * @param time
             *     Describes the time last calibration has been performed
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #time(org.linuxforhealth.fhir.model.type.Instant)
             */
            public Builder time(java.time.ZonedDateTime time) {
                this.time = (time == null) ? null : Instant.of(time);
                return this;
            }

            /**
             * Describes the time last calibration has been performed.
             * 
             * @param time
             *     Describes the time last calibration has been performed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder time(Instant time) {
                this.time = time;
                return this;
            }

            /**
             * Build the {@link Calibration}
             * 
             * @return
             *     An immutable object of type {@link Calibration}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Calibration per the base specification
             */
            @Override
            public Calibration build() {
                Calibration calibration = new Calibration(this);
                if (validating) {
                    validate(calibration);
                }
                return calibration;
            }

            protected void validate(Calibration calibration) {
                super.validate(calibration);
                ValidationSupport.requireValueOrChildren(calibration);
            }

            protected Builder from(Calibration calibration) {
                super.from(calibration);
                type = calibration.type;
                state = calibration.state;
                time = calibration.time;
                return this;
            }
        }
    }
}
