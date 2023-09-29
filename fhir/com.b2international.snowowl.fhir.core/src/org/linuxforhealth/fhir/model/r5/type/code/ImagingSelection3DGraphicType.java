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

@System("http://hl7.org/fhir/imagingselection-3dgraphictype")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ImagingSelection3DGraphicType extends Code {
    /**
     * POINT
     * 
     * <p>A single location denoted by a single (x,y,z) triplet.
     */
    public static final ImagingSelection3DGraphicType POINT = ImagingSelection3DGraphicType.builder().value(Value.POINT).build();

    /**
     * MULTIPOINT
     * 
     * <p>multiple locations each denoted by an (x,y,z) triplet; the points need not be coplanar.
     */
    public static final ImagingSelection3DGraphicType MULTIPOINT = ImagingSelection3DGraphicType.builder().value(Value.MULTIPOINT).build();

    /**
     * POLYLINE
     * 
     * <p>a series of connected line segments with ordered vertices denoted by (x,y,z) triplets; the points need not be 
     * coplanar.
     */
    public static final ImagingSelection3DGraphicType POLYLINE = ImagingSelection3DGraphicType.builder().value(Value.POLYLINE).build();

    /**
     * POLYGON
     * 
     * <p>a series of connected line segments with ordered vertices denoted by (x,y,z) triplets, where the first and last 
     * vertices shall be the same forming a polygon; the points shall be coplanar.
     */
    public static final ImagingSelection3DGraphicType POLYGON = ImagingSelection3DGraphicType.builder().value(Value.POLYGON).build();

    /**
     * ELLIPSE
     * 
     * <p>an ellipse defined by four (x,y,z) triplets, the first two triplets specifying the endpoints of the major axis and 
     * the second two triplets specifying the endpoints of the minor axis.
     */
    public static final ImagingSelection3DGraphicType ELLIPSE = ImagingSelection3DGraphicType.builder().value(Value.ELLIPSE).build();

    /**
     * ELLIPSOID
     * 
     * <p>a three-dimensional geometric surface whose plane sections are either ellipses or circles and contains three 
     * intersecting orthogonal axes, "a", "b", and "c"; the ellipsoid is defined by six (x,y,z) triplets, the first and 
     * second triplets specifying the endpoints of axis "a", the third and fourth triplets specifying the endpoints of axis 
     * "b", and the fifth and sixth triplets specifying the endpoints of axis "c".
     */
    public static final ImagingSelection3DGraphicType ELLIPSOID = ImagingSelection3DGraphicType.builder().value(Value.ELLIPSOID).build();

    private volatile int hashCode;

    private ImagingSelection3DGraphicType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ImagingSelection3DGraphicType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ImagingSelection3DGraphicType objects from a passed enum value.
     */
    public static ImagingSelection3DGraphicType of(Value value) {
        switch (value) {
        case POINT:
            return POINT;
        case MULTIPOINT:
            return MULTIPOINT;
        case POLYLINE:
            return POLYLINE;
        case POLYGON:
            return POLYGON;
        case ELLIPSE:
            return ELLIPSE;
        case ELLIPSOID:
            return ELLIPSOID;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ImagingSelection3DGraphicType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ImagingSelection3DGraphicType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ImagingSelection3DGraphicType objects from a passed string value.
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
     * Inherited factory method for creating ImagingSelection3DGraphicType objects from a passed string value.
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
        ImagingSelection3DGraphicType other = (ImagingSelection3DGraphicType) obj;
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
         *     An enum constant for ImagingSelection3DGraphicType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ImagingSelection3DGraphicType build() {
            ImagingSelection3DGraphicType imagingSelection3DGraphicType = new ImagingSelection3DGraphicType(this);
            if (validating) {
                validate(imagingSelection3DGraphicType);
            }
            return imagingSelection3DGraphicType;
        }

        protected void validate(ImagingSelection3DGraphicType imagingSelection3DGraphicType) {
            super.validate(imagingSelection3DGraphicType);
        }

        protected Builder from(ImagingSelection3DGraphicType imagingSelection3DGraphicType) {
            super.from(imagingSelection3DGraphicType);
            return this;
        }
    }

    public enum Value {
        /**
         * POINT
         * 
         * <p>A single location denoted by a single (x,y,z) triplet.
         */
        POINT("point"),

        /**
         * MULTIPOINT
         * 
         * <p>multiple locations each denoted by an (x,y,z) triplet; the points need not be coplanar.
         */
        MULTIPOINT("multipoint"),

        /**
         * POLYLINE
         * 
         * <p>a series of connected line segments with ordered vertices denoted by (x,y,z) triplets; the points need not be 
         * coplanar.
         */
        POLYLINE("polyline"),

        /**
         * POLYGON
         * 
         * <p>a series of connected line segments with ordered vertices denoted by (x,y,z) triplets, where the first and last 
         * vertices shall be the same forming a polygon; the points shall be coplanar.
         */
        POLYGON("polygon"),

        /**
         * ELLIPSE
         * 
         * <p>an ellipse defined by four (x,y,z) triplets, the first two triplets specifying the endpoints of the major axis and 
         * the second two triplets specifying the endpoints of the minor axis.
         */
        ELLIPSE("ellipse"),

        /**
         * ELLIPSOID
         * 
         * <p>a three-dimensional geometric surface whose plane sections are either ellipses or circles and contains three 
         * intersecting orthogonal axes, "a", "b", and "c"; the ellipsoid is defined by six (x,y,z) triplets, the first and 
         * second triplets specifying the endpoints of axis "a", the third and fourth triplets specifying the endpoints of axis 
         * "b", and the fifth and sixth triplets specifying the endpoints of axis "c".
         */
        ELLIPSOID("ellipsoid");

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
         * Factory method for creating ImagingSelection3DGraphicType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ImagingSelection3DGraphicType.Value or null if a null value was passed
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
            case "multipoint":
                return MULTIPOINT;
            case "polyline":
                return POLYLINE;
            case "polygon":
                return POLYGON;
            case "ellipse":
                return ELLIPSE;
            case "ellipsoid":
                return ELLIPSOID;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
