/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource reflects an instance of a biologically derived product. A material substance originating from a 
 * biological entity intended to be transplanted or infused
into another (possibly the same) biological entity.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class BiologicallyDerivedProduct extends DomainResource {
    @Binding(
        bindingName = "BiologicallyDerivedProductCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Biologically Derived Product Category.",
        valueSet = "http://hl7.org/fhir/ValueSet/product-category"
    )
    private final Coding productCategory;
    @Binding(
        bindingName = "BiologicallyDerivedProductCodes",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Biologically-derived Product Codes",
        valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderived-productcodes"
    )
    private final CodeableConcept productCode;
    @ReferenceTarget({ "BiologicallyDerivedProduct" })
    private final List<Reference> parent;
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> request;
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final Identifier biologicalSourceEvent;
    @ReferenceTarget({ "Organization" })
    private final List<Reference> processingFacility;
    private final String division;
    @Binding(
        bindingName = "BiologicallyDerivedProductStatus",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Biologically Derived Product Status.",
        valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderived-product-status"
    )
    private final Coding productStatus;
    private final DateTime expirationDate;
    private final Collection collection;
    private final Range storageTempRequirements;
    private final List<Property> property;

    private BiologicallyDerivedProduct(Builder builder) {
        super(builder);
        productCategory = builder.productCategory;
        productCode = builder.productCode;
        parent = Collections.unmodifiableList(builder.parent);
        request = Collections.unmodifiableList(builder.request);
        identifier = Collections.unmodifiableList(builder.identifier);
        biologicalSourceEvent = builder.biologicalSourceEvent;
        processingFacility = Collections.unmodifiableList(builder.processingFacility);
        division = builder.division;
        productStatus = builder.productStatus;
        expirationDate = builder.expirationDate;
        collection = builder.collection;
        storageTempRequirements = builder.storageTempRequirements;
        property = Collections.unmodifiableList(builder.property);
    }

    /**
     * Broad category of this product.
     * 
     * @return
     *     An immutable object of type {@link Coding} that may be null.
     */
    public Coding getProductCategory() {
        return productCategory;
    }

    /**
     * A codified value that systematically supports characterization and classification of medical products of human origin 
     * inclusive of processing conditions such as additives, volumes and handling conditions.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getProductCode() {
        return productCode;
    }

    /**
     * Parent product (if any) for this biologically-derived product.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getParent() {
        return parent;
    }

    /**
     * Request to obtain and/or infuse this biologically derived product.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRequest() {
        return request;
    }

    /**
     * Unique instance identifiers assigned to a biologically derived product. Note: This is a business identifier, not a 
     * resource identifier.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * An identifier that supports traceability to the event during which material in this product from one or more 
     * biological entities was obtained or pooled.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getBiologicalSourceEvent() {
        return biologicalSourceEvent;
    }

    /**
     * Processing facilities responsible for the labeling and distribution of this biologically derived product.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getProcessingFacility() {
        return processingFacility;
    }

    /**
     * A unique identifier for an aliquot of a product. Used to distinguish individual aliquots of a product carrying the 
     * same biologicalSource and productCode identifiers.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDivision() {
        return division;
    }

    /**
     * Whether the product is currently available.
     * 
     * @return
     *     An immutable object of type {@link Coding} that may be null.
     */
    public Coding getProductStatus() {
        return productStatus;
    }

    /**
     * Date, and where relevant time, of expiration.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getExpirationDate() {
        return expirationDate;
    }

    /**
     * How this product was collected.
     * 
     * @return
     *     An immutable object of type {@link Collection} that may be null.
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * The temperature requirements for storage of the biologically-derived product.
     * 
     * @return
     *     An immutable object of type {@link Range} that may be null.
     */
    public Range getStorageTempRequirements() {
        return storageTempRequirements;
    }

    /**
     * A property that is specific to this BiologicallyDerviedProduct instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (productCategory != null) || 
            (productCode != null) || 
            !parent.isEmpty() || 
            !request.isEmpty() || 
            !identifier.isEmpty() || 
            (biologicalSourceEvent != null) || 
            !processingFacility.isEmpty() || 
            (division != null) || 
            (productStatus != null) || 
            (expirationDate != null) || 
            (collection != null) || 
            (storageTempRequirements != null) || 
            !property.isEmpty();
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
                accept(productCategory, "productCategory", visitor);
                accept(productCode, "productCode", visitor);
                accept(parent, "parent", visitor, Reference.class);
                accept(request, "request", visitor, Reference.class);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(biologicalSourceEvent, "biologicalSourceEvent", visitor);
                accept(processingFacility, "processingFacility", visitor, Reference.class);
                accept(division, "division", visitor);
                accept(productStatus, "productStatus", visitor);
                accept(expirationDate, "expirationDate", visitor);
                accept(collection, "collection", visitor);
                accept(storageTempRequirements, "storageTempRequirements", visitor);
                accept(property, "property", visitor, Property.class);
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
        BiologicallyDerivedProduct other = (BiologicallyDerivedProduct) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(productCategory, other.productCategory) && 
            Objects.equals(productCode, other.productCode) && 
            Objects.equals(parent, other.parent) && 
            Objects.equals(request, other.request) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(biologicalSourceEvent, other.biologicalSourceEvent) && 
            Objects.equals(processingFacility, other.processingFacility) && 
            Objects.equals(division, other.division) && 
            Objects.equals(productStatus, other.productStatus) && 
            Objects.equals(expirationDate, other.expirationDate) && 
            Objects.equals(collection, other.collection) && 
            Objects.equals(storageTempRequirements, other.storageTempRequirements) && 
            Objects.equals(property, other.property);
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
                productCategory, 
                productCode, 
                parent, 
                request, 
                identifier, 
                biologicalSourceEvent, 
                processingFacility, 
                division, 
                productStatus, 
                expirationDate, 
                collection, 
                storageTempRequirements, 
                property);
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
        private Coding productCategory;
        private CodeableConcept productCode;
        private List<Reference> parent = new ArrayList<>();
        private List<Reference> request = new ArrayList<>();
        private List<Identifier> identifier = new ArrayList<>();
        private Identifier biologicalSourceEvent;
        private List<Reference> processingFacility = new ArrayList<>();
        private String division;
        private Coding productStatus;
        private DateTime expirationDate;
        private Collection collection;
        private Range storageTempRequirements;
        private List<Property> property = new ArrayList<>();

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
        public Builder contained(java.util.Collection<Resource> contained) {
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
        public Builder extension(java.util.Collection<Extension> extension) {
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
        public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * Broad category of this product.
         * 
         * @param productCategory
         *     organ | tissue | fluid | cells | biologicalAgent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productCategory(Coding productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        /**
         * A codified value that systematically supports characterization and classification of medical products of human origin 
         * inclusive of processing conditions such as additives, volumes and handling conditions.
         * 
         * @param productCode
         *     A code that identifies the kind of this biologically derived product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productCode(CodeableConcept productCode) {
            this.productCode = productCode;
            return this;
        }

        /**
         * Parent product (if any) for this biologically-derived product.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * </ul>
         * 
         * @param parent
         *     The parent biologically-derived product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parent(Reference... parent) {
            for (Reference value : parent) {
                this.parent.add(value);
            }
            return this;
        }

        /**
         * Parent product (if any) for this biologically-derived product.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * </ul>
         * 
         * @param parent
         *     The parent biologically-derived product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder parent(java.util.Collection<Reference> parent) {
            this.parent = new ArrayList<>(parent);
            return this;
        }

        /**
         * Request to obtain and/or infuse this biologically derived product.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param request
         *     Request to obtain and/or infuse this product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder request(Reference... request) {
            for (Reference value : request) {
                this.request.add(value);
            }
            return this;
        }

        /**
         * Request to obtain and/or infuse this biologically derived product.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param request
         *     Request to obtain and/or infuse this product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder request(java.util.Collection<Reference> request) {
            this.request = new ArrayList<>(request);
            return this;
        }

        /**
         * Unique instance identifiers assigned to a biologically derived product. Note: This is a business identifier, not a 
         * resource identifier.
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
         * Unique instance identifiers assigned to a biologically derived product. Note: This is a business identifier, not a 
         * resource identifier.
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
        public Builder identifier(java.util.Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * An identifier that supports traceability to the event during which material in this product from one or more 
         * biological entities was obtained or pooled.
         * 
         * @param biologicalSourceEvent
         *     An identifier that supports traceability to the event during which material in this product from one or more 
         *     biological entities was obtained or pooled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder biologicalSourceEvent(Identifier biologicalSourceEvent) {
            this.biologicalSourceEvent = biologicalSourceEvent;
            return this;
        }

        /**
         * Processing facilities responsible for the labeling and distribution of this biologically derived product.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param processingFacility
         *     Processing facilities responsible for the labeling and distribution of this biologically derived product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder processingFacility(Reference... processingFacility) {
            for (Reference value : processingFacility) {
                this.processingFacility.add(value);
            }
            return this;
        }

        /**
         * Processing facilities responsible for the labeling and distribution of this biologically derived product.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param processingFacility
         *     Processing facilities responsible for the labeling and distribution of this biologically derived product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder processingFacility(java.util.Collection<Reference> processingFacility) {
            this.processingFacility = new ArrayList<>(processingFacility);
            return this;
        }

        /**
         * Convenience method for setting {@code division}.
         * 
         * @param division
         *     A unique identifier for an aliquot of a product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #division(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder division(java.lang.String division) {
            this.division = (division == null) ? null : String.of(division);
            return this;
        }

        /**
         * A unique identifier for an aliquot of a product. Used to distinguish individual aliquots of a product carrying the 
         * same biologicalSource and productCode identifiers.
         * 
         * @param division
         *     A unique identifier for an aliquot of a product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder division(String division) {
            this.division = division;
            return this;
        }

        /**
         * Whether the product is currently available.
         * 
         * @param productStatus
         *     available | unavailable
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productStatus(Coding productStatus) {
            this.productStatus = productStatus;
            return this;
        }

        /**
         * Date, and where relevant time, of expiration.
         * 
         * @param expirationDate
         *     Date, and where relevant time, of expiration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expirationDate(DateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        /**
         * How this product was collected.
         * 
         * @param collection
         *     How this product was collected
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder collection(Collection collection) {
            this.collection = collection;
            return this;
        }

        /**
         * The temperature requirements for storage of the biologically-derived product.
         * 
         * @param storageTempRequirements
         *     Product storage temperature requirements
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder storageTempRequirements(Range storageTempRequirements) {
            this.storageTempRequirements = storageTempRequirements;
            return this;
        }

        /**
         * A property that is specific to this BiologicallyDerviedProduct instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     A property that is specific to this BiologicallyDerviedProduct instance
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder property(Property... property) {
            for (Property value : property) {
                this.property.add(value);
            }
            return this;
        }

        /**
         * A property that is specific to this BiologicallyDerviedProduct instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     A property that is specific to this BiologicallyDerviedProduct instance
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder property(java.util.Collection<Property> property) {
            this.property = new ArrayList<>(property);
            return this;
        }

        /**
         * Build the {@link BiologicallyDerivedProduct}
         * 
         * @return
         *     An immutable object of type {@link BiologicallyDerivedProduct}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid BiologicallyDerivedProduct per the base specification
         */
        @Override
        public BiologicallyDerivedProduct build() {
            BiologicallyDerivedProduct biologicallyDerivedProduct = new BiologicallyDerivedProduct(this);
            if (validating) {
                validate(biologicallyDerivedProduct);
            }
            return biologicallyDerivedProduct;
        }

        protected void validate(BiologicallyDerivedProduct biologicallyDerivedProduct) {
            super.validate(biologicallyDerivedProduct);
            ValidationSupport.checkList(biologicallyDerivedProduct.parent, "parent", Reference.class);
            ValidationSupport.checkList(biologicallyDerivedProduct.request, "request", Reference.class);
            ValidationSupport.checkList(biologicallyDerivedProduct.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(biologicallyDerivedProduct.processingFacility, "processingFacility", Reference.class);
            ValidationSupport.checkList(biologicallyDerivedProduct.property, "property", Property.class);
            ValidationSupport.checkReferenceType(biologicallyDerivedProduct.parent, "parent", "BiologicallyDerivedProduct");
            ValidationSupport.checkReferenceType(biologicallyDerivedProduct.request, "request", "ServiceRequest");
            ValidationSupport.checkReferenceType(biologicallyDerivedProduct.processingFacility, "processingFacility", "Organization");
        }

        protected Builder from(BiologicallyDerivedProduct biologicallyDerivedProduct) {
            super.from(biologicallyDerivedProduct);
            productCategory = biologicallyDerivedProduct.productCategory;
            productCode = biologicallyDerivedProduct.productCode;
            parent.addAll(biologicallyDerivedProduct.parent);
            request.addAll(biologicallyDerivedProduct.request);
            identifier.addAll(biologicallyDerivedProduct.identifier);
            biologicalSourceEvent = biologicallyDerivedProduct.biologicalSourceEvent;
            processingFacility.addAll(biologicallyDerivedProduct.processingFacility);
            division = biologicallyDerivedProduct.division;
            productStatus = biologicallyDerivedProduct.productStatus;
            expirationDate = biologicallyDerivedProduct.expirationDate;
            collection = biologicallyDerivedProduct.collection;
            storageTempRequirements = biologicallyDerivedProduct.storageTempRequirements;
            property.addAll(biologicallyDerivedProduct.property);
            return this;
        }
    }

    /**
     * How this product was collected.
     */
    public static class Collection extends BackboneElement {
        @ReferenceTarget({ "Practitioner", "PractitionerRole" })
        private final Reference collector;
        @ReferenceTarget({ "Patient", "Organization" })
        private final Reference source;
        @Choice({ DateTime.class, Period.class })
        private final Element collected;

        private Collection(Builder builder) {
            super(builder);
            collector = builder.collector;
            source = builder.source;
            collected = builder.collected;
        }

        /**
         * Healthcare professional who is performing the collection.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getCollector() {
            return collector;
        }

        /**
         * The patient or entity, such as a hospital or vendor in the case of a processed/manipulated/manufactured product, 
         * providing the product.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSource() {
            return source;
        }

        /**
         * Time of product collection.
         * 
         * @return
         *     An immutable object of type {@link DateTime} or {@link Period} that may be null.
         */
        public Element getCollected() {
            return collected;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (collector != null) || 
                (source != null) || 
                (collected != null);
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
                    accept(collector, "collector", visitor);
                    accept(source, "source", visitor);
                    accept(collected, "collected", visitor);
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
            Collection other = (Collection) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(collector, other.collector) && 
                Objects.equals(source, other.source) && 
                Objects.equals(collected, other.collected);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    collector, 
                    source, 
                    collected);
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
            private Reference collector;
            private Reference source;
            private Element collected;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * Healthcare professional who is performing the collection.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * </ul>
             * 
             * @param collector
             *     Individual performing collection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder collector(Reference collector) {
                this.collector = collector;
                return this;
            }

            /**
             * The patient or entity, such as a hospital or vendor in the case of a processed/manipulated/manufactured product, 
             * providing the product.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param source
             *     The patient who underwent the medical procedure to collect the product or the organization that facilitated the 
             *     collection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(Reference source) {
                this.source = source;
                return this;
            }

            /**
             * Time of product collection.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link DateTime}</li>
             * <li>{@link Period}</li>
             * </ul>
             * 
             * @param collected
             *     Time of product collection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder collected(Element collected) {
                this.collected = collected;
                return this;
            }

            /**
             * Build the {@link Collection}
             * 
             * @return
             *     An immutable object of type {@link Collection}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Collection per the base specification
             */
            @Override
            public Collection build() {
                Collection collection = new Collection(this);
                if (validating) {
                    validate(collection);
                }
                return collection;
            }

            protected void validate(Collection collection) {
                super.validate(collection);
                ValidationSupport.choiceElement(collection.collected, "collected", DateTime.class, Period.class);
                ValidationSupport.checkReferenceType(collection.collector, "collector", "Practitioner", "PractitionerRole");
                ValidationSupport.checkReferenceType(collection.source, "source", "Patient", "Organization");
                ValidationSupport.requireValueOrChildren(collection);
            }

            protected Builder from(Collection collection) {
                super.from(collection);
                collector = collection.collector;
                source = collection.source;
                collected = collection.collected;
                return this;
            }
        }
    }

    /**
     * A property that is specific to this BiologicallyDerviedProduct instance.
     */
    public static class Property extends BackboneElement {
        @Binding(
            bindingName = "BiologicallyDerivedProductPropertyTypeCodes",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Biologically Derived Product Property Type Codes",
            valueSet = "http://hl7.org/fhir/ValueSet/biologicallyderived-product-property-type-codes"
        )
        @Required
        private final CodeableConcept type;
        @Choice({ Boolean.class, Integer.class, CodeableConcept.class, Period.class, Quantity.class, Range.class, Ratio.class, String.class, Attachment.class })
        @Required
        private final Element value;

        private Property(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * Code that specifies the property. It should reference an established coding system.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Property values.
         * 
         * @return
         *     An immutable object of type {@link Boolean}, {@link Integer}, {@link CodeableConcept}, {@link Period}, {@link 
         *     Quantity}, {@link Range}, {@link Ratio}, {@link String} or {@link Attachment} that is non-null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
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
                    accept(type, "type", visitor);
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
            Property other = (Property) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
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
            private CodeableConcept type;
            private Element value;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * Code that specifies the property. It should reference an established coding system.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Code that specifies the property
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Property values
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Boolean value) {
                this.value = (value == null) ? null : Boolean.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Property values
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Integer value) {
                this.value = (value == null) ? null : Integer.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type String.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Property values
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * Property values.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Boolean}</li>
             * <li>{@link Integer}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Period}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Ratio}</li>
             * <li>{@link String}</li>
             * <li>{@link Attachment}</li>
             * </ul>
             * 
             * @param value
             *     Property values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Property}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Property}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Property per the base specification
             */
            @Override
            public Property build() {
                Property property = new Property(this);
                if (validating) {
                    validate(property);
                }
                return property;
            }

            protected void validate(Property property) {
                super.validate(property);
                ValidationSupport.requireNonNull(property.type, "type");
                ValidationSupport.requireChoiceElement(property.value, "value", Boolean.class, Integer.class, CodeableConcept.class, Period.class, Quantity.class, Range.class, Ratio.class, String.class, Attachment.class);
                ValidationSupport.requireValueOrChildren(property);
            }

            protected Builder from(Property property) {
                super.from(property);
                type = property.type;
                value = property.value;
                return this;
            }
        }
    }
}
