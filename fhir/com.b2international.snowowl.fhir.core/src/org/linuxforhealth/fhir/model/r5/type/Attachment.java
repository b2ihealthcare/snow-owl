/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * For referring to data content defined in other formats.
 */
@Constraint(
    id = "att-1",
    level = "Rule",
    location = "(base)",
    description = "If the Attachment has data, it SHALL have a contentType",
    expression = "data.empty() or contentType.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Attachment"
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Attachment extends DataType {
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
        bindingName = "Language",
        strength = BindingStrength.Value.REQUIRED,
        description = "IETF language tag for a human language.",
        valueSet = "http://hl7.org/fhir/ValueSet/all-languages|5.0.0"
    )
    private final Code language;
    private final Base64Binary data;
    @Summary
    private final Url url;
    @Summary
    private final Integer64 size;
    @Summary
    private final Base64Binary hash;
    @Summary
    private final String title;
    @Summary
    private final DateTime creation;
    private final PositiveInt height;
    private final PositiveInt width;
    private final PositiveInt frames;
    private final Decimal duration;
    private final PositiveInt pages;

    private Attachment(Builder builder) {
        super(builder);
        contentType = builder.contentType;
        language = builder.language;
        data = builder.data;
        url = builder.url;
        size = builder.size;
        hash = builder.hash;
        title = builder.title;
        creation = builder.creation;
        height = builder.height;
        width = builder.width;
        frames = builder.frames;
        duration = builder.duration;
        pages = builder.pages;
    }

    /**
     * Identifies the type of the data in the attachment and allows a method to be chosen to interpret or render the data. 
     * Includes mime type parameters such as charset where appropriate.
     * 
     * @return
     *     An immutable object of type {@link Code} that may be null.
     */
    public Code getContentType() {
        return contentType;
    }

    /**
     * The human language of the content. The value can be any valid value according to BCP 47.
     * 
     * @return
     *     An immutable object of type {@link Code} that may be null.
     */
    public Code getLanguage() {
        return language;
    }

    /**
     * The actual data of the attachment - a sequence of bytes, base64 encoded.
     * 
     * @return
     *     An immutable object of type {@link Base64Binary} that may be null.
     */
    public Base64Binary getData() {
        return data;
    }

    /**
     * A location where the data can be accessed.
     * 
     * @return
     *     An immutable object of type {@link Url} that may be null.
     */
    public Url getUrl() {
        return url;
    }

    /**
     * The number of bytes of data that make up this attachment (before base64 encoding, if that is done).
     * 
     * @return
     *     An immutable object of type {@link Integer64} that may be null.
     */
    public Integer64 getSize() {
        return size;
    }

    /**
     * The calculated hash of the data using SHA-1. Represented using base64.
     * 
     * @return
     *     An immutable object of type {@link Base64Binary} that may be null.
     */
    public Base64Binary getHash() {
        return hash;
    }

    /**
     * A label or set of text to display in place of the data.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The date that the attachment was first created.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getCreation() {
        return creation;
    }

    /**
     * Height of the image in pixels (photo/video).
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getHeight() {
        return height;
    }

    /**
     * Width of the image in pixels (photo/video).
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getWidth() {
        return width;
    }

    /**
     * The number of frames in a photo. This is used with a multi-page fax, or an imaging acquisition context that takes 
     * multiple slices in a single image, or an animated gif. If there is more than one frame, this SHALL have a value in 
     * order to alert interface software that a multi-frame capable rendering widget is required.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getFrames() {
        return frames;
    }

    /**
     * The duration of the recording in seconds - for audio and video.
     * 
     * @return
     *     An immutable object of type {@link Decimal} that may be null.
     */
    public Decimal getDuration() {
        return duration;
    }

    /**
     * The number of pages when printed.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getPages() {
        return pages;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (contentType != null) || 
            (language != null) || 
            (data != null) || 
            (url != null) || 
            (size != null) || 
            (hash != null) || 
            (title != null) || 
            (creation != null) || 
            (height != null) || 
            (width != null) || 
            (frames != null) || 
            (duration != null) || 
            (pages != null);
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(contentType, "contentType", visitor);
                accept(language, "language", visitor);
                accept(data, "data", visitor);
                accept(url, "url", visitor);
                accept(size, "size", visitor);
                accept(hash, "hash", visitor);
                accept(title, "title", visitor);
                accept(creation, "creation", visitor);
                accept(height, "height", visitor);
                accept(width, "width", visitor);
                accept(frames, "frames", visitor);
                accept(duration, "duration", visitor);
                accept(pages, "pages", visitor);
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
        Attachment other = (Attachment) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(contentType, other.contentType) && 
            Objects.equals(language, other.language) && 
            Objects.equals(data, other.data) && 
            Objects.equals(url, other.url) && 
            Objects.equals(size, other.size) && 
            Objects.equals(hash, other.hash) && 
            Objects.equals(title, other.title) && 
            Objects.equals(creation, other.creation) && 
            Objects.equals(height, other.height) && 
            Objects.equals(width, other.width) && 
            Objects.equals(frames, other.frames) && 
            Objects.equals(duration, other.duration) && 
            Objects.equals(pages, other.pages);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                contentType, 
                language, 
                data, 
                url, 
                size, 
                hash, 
                title, 
                creation, 
                height, 
                width, 
                frames, 
                duration, 
                pages);
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
        private Code contentType;
        private Code language;
        private Base64Binary data;
        private Url url;
        private Integer64 size;
        private Base64Binary hash;
        private String title;
        private DateTime creation;
        private PositiveInt height;
        private PositiveInt width;
        private PositiveInt frames;
        private Decimal duration;
        private PositiveInt pages;

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
         * Identifies the type of the data in the attachment and allows a method to be chosen to interpret or render the data. 
         * Includes mime type parameters such as charset where appropriate.
         * 
         * @param contentType
         *     Mime type of the content, with charset etc.
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contentType(Code contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * The human language of the content. The value can be any valid value according to BCP 47.
         * 
         * @param language
         *     Human language of the content (BCP-47)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder language(Code language) {
            this.language = language;
            return this;
        }

        /**
         * The actual data of the attachment - a sequence of bytes, base64 encoded.
         * 
         * @param data
         *     Data inline, base64ed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder data(Base64Binary data) {
            this.data = data;
            return this;
        }

        /**
         * A location where the data can be accessed.
         * 
         * @param url
         *     Uri where the data can be found
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Url url) {
            this.url = url;
            return this;
        }

        /**
         * The number of bytes of data that make up this attachment (before base64 encoding, if that is done).
         * 
         * @param size
         *     Number of bytes of content (if url provided)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder size(Integer64 size) {
            this.size = size;
            return this;
        }

        /**
         * The calculated hash of the data using SHA-1. Represented using base64.
         * 
         * @param hash
         *     Hash of the data (sha-1, base64ed)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder hash(Base64Binary hash) {
            this.hash = hash;
            return this;
        }

        /**
         * Convenience method for setting {@code title}.
         * 
         * @param title
         *     Label to display in place of the data
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #title(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder title(java.lang.String title) {
            this.title = (title == null) ? null : String.of(title);
            return this;
        }

        /**
         * A label or set of text to display in place of the data.
         * 
         * @param title
         *     Label to display in place of the data
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The date that the attachment was first created.
         * 
         * @param creation
         *     Date attachment was first created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder creation(DateTime creation) {
            this.creation = creation;
            return this;
        }

        /**
         * Height of the image in pixels (photo/video).
         * 
         * @param height
         *     Height of the image in pixels (photo/video)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder height(PositiveInt height) {
            this.height = height;
            return this;
        }

        /**
         * Width of the image in pixels (photo/video).
         * 
         * @param width
         *     Width of the image in pixels (photo/video)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder width(PositiveInt width) {
            this.width = width;
            return this;
        }

        /**
         * The number of frames in a photo. This is used with a multi-page fax, or an imaging acquisition context that takes 
         * multiple slices in a single image, or an animated gif. If there is more than one frame, this SHALL have a value in 
         * order to alert interface software that a multi-frame capable rendering widget is required.
         * 
         * @param frames
         *     Number of frames if &gt; 1 (photo)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder frames(PositiveInt frames) {
            this.frames = frames;
            return this;
        }

        /**
         * The duration of the recording in seconds - for audio and video.
         * 
         * @param duration
         *     Length in seconds (audio / video)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder duration(Decimal duration) {
            this.duration = duration;
            return this;
        }

        /**
         * The number of pages when printed.
         * 
         * @param pages
         *     Number of printed pages
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder pages(PositiveInt pages) {
            this.pages = pages;
            return this;
        }

        /**
         * Build the {@link Attachment}
         * 
         * @return
         *     An immutable object of type {@link Attachment}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Attachment per the base specification
         */
        @Override
        public Attachment build() {
            Attachment attachment = new Attachment(this);
            if (validating) {
                validate(attachment);
            }
            return attachment;
        }

        protected void validate(Attachment attachment) {
            super.validate(attachment);
            ValidationSupport.checkValueSetBinding(attachment.language, "language", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
            ValidationSupport.requireValueOrChildren(attachment);
        }

        protected Builder from(Attachment attachment) {
            super.from(attachment);
            contentType = attachment.contentType;
            language = attachment.language;
            data = attachment.data;
            url = attachment.url;
            size = attachment.size;
            hash = attachment.hash;
            title = attachment.title;
            creation = attachment.creation;
            height = attachment.height;
            width = attachment.width;
            frames = attachment.frames;
            duration = attachment.duration;
            pages = attachment.pages;
            return this;
        }
    }
}
