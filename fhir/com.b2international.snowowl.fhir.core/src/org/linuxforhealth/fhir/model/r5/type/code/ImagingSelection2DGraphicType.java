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

@System("http://hl7.org/fhir/imagingselection-2dgraphictype")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ImagingSelection2DGraphicType extends Code {
    /**
     * POINT
     * 
     * <p>A single location denoted by a single (x,y) pair.
     */
    public static final ImagingSelection2DGraphicType POINT = ImagingSelection2DGraphicType.builder().value(Value.POINT).build();

    /**
     * POLYLINE
     * 
     * <p>A series of connected line segments with ordered vertices denoted by (x,y) triplets; the points need not be 
     * coplanar.
     */
    public static final ImagingSelection2DGraphicType POLYLINE = ImagingSelection2DGraphicType.builder().value(Value.POLYLINE).build();

    /**
     * INTERPOLATED
     * 
     * <p>An n-tuple list of (x,y) pair end points between which some form of implementation dependent curved lines are to be 
     * drawn. The rendered line shall pass through all the specified points.
     */
    public static final ImagingSelection2DGraphicType INTERPOLATED = ImagingSelection2DGraphicType.builder().value(Value.INTERPOLATED).build();

    /**
     * CIRCLE
     * 
     * <p>Two points shall be present; the first point is to be interpreted as the center and the second point as a point on 
     * the circumference of a circle, some form of implementation dependent representation of which is to be drawn.
     */
    public static final ImagingSelection2DGraphicType CIRCLE = ImagingSelection2DGraphicType.builder().value(Value.CIRCLE).build();

    /**
     * ELLIPSE
     * 
     * <p>An ellipse defined by four (x,y) pairs, the first two pairs specifying the endpoints of the major axis and the 
     * second two pairs specifying the endpoints of the minor axis.
     */
    public static final ImagingSelection2DGraphicType ELLIPSE = ImagingSelection2DGraphicType.builder().value(Value.ELLIPSE).build();

    private volatile int hashCode;

    private ImagingSelection2DGraphicType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ImagingSelection2DGraphicType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ImagingSelection2DGraphicType objects from a passed enum value.
     */
    public static ImagingSelection2DGraphicType of(Value value) {
        switch (value) {
        case POINT:
            return POINT;
        case POLYLINE:
            return POLYLINE;
        case INTERPOLATED:
            return INTERPOLATED;
        case CIRCLE:
            return CIRCLE;
        case ELLIPSE:
            return ELLIPSE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ImagingSelection2DGraphicType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ImagingSelection2DGraphicType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ImagingSelection2DGraphicType objects from a passed string value.
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
     * Inherited factory method for creating ImagingSelection2DGraphicType objects from a passed string value.
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
        ImagingSelection2DGraphicType other = (ImagingSelection2DGraphicType) obj;
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
         *     An enum constant for ImagingSelection2DGraphicType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ImagingSelection2DGraphicType build() {
            ImagingSelection2DGraphicType imagingSelection2DGraphicType = new ImagingSelection2DGraphicType(this);
            if (validating) {
                validate(imagingSelection2DGraphicType);
            }
            return imagingSelection2DGraphicType;
        }

        protected void validate(ImagingSelection2DGraphicType imagingSelection2DGraphicType) {
            super.validate(imagingSelection2DGraphicType);
        }

        protected Builder from(ImagingSelection2DGraphicType imagingSelection2DGraphicType) {
            super.from(imagingSelection2DGraphicType);
            return this;
        }
    }

    public enum Value {
        /**
         * POINT
         * 
         * <p>A single location denoted by a single (x,y) pair.
         */
        POINT("point"),

        /**
         * POLYLINE
         * 
         * <p>A series of connected line segments with ordered vertices denoted by (x,y) triplets; the points need not be 
         * coplanar.
         */
        POLYLINE("polyline"),

        /**
         * INTERPOLATED
         * 
         * <p>An n-tuple list of (x,y) pair end points between which some form of implementation dependent curved lines are to be 
         * drawn. The rendered line shall pass through all the specified points.
         */
        INTERPOLATED("interpolated"),

        /**
         * CIRCLE
         * 
         * <p>Two points shall be present; the first point is to be interpreted as the center and the second point as a point on 
         * the circumference of a circle, some form of implementation dependent representation of which is to be drawn.
         */
        CIRCLE("circle"),

        /**
         * ELLIPSE
         * 
         * <p>An ellipse defined by four (x,y) pairs, the first two pairs specifying the endpoints of the major axis and the 
         * second two pairs specifying the endpoints of the minor axis.
         */
        ELLIPSE("ellipse");

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
         * Factory method for creating ImagingSelection2DGraphicType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ImagingSelection2DGraphicType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "point":
                return POINT;
            case "polyline":
                return POLYLINE;
            case "interpolated":
                return INTERPOLATED;
            case "circle":
                return CIRCLE;
            case "ellipse":
                return ELLIPSE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
