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
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Base64Binary;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MedicationKnowledgeStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Information about a medication that is used to support knowledge.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MedicationKnowledge extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "MedicationFormalRepresentation",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept that defines the type of a medication.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    private final CodeableConcept code;
    @Summary
    @Binding(
        bindingName = "MedicationKnowledgeStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept defining if the medication is in active use.",
        valueSet = "http://hl7.org/fhir/ValueSet/medicationknowledge-status|5.0.0"
    )
    private final MedicationKnowledgeStatus status;
    @ReferenceTarget({ "Organization" })
    private final Reference author;
    private final List<CodeableConcept> intendedJurisdiction;
    @Summary
    private final List<String> name;
    private final List<RelatedMedicationKnowledge> relatedMedicationKnowledge;
    @ReferenceTarget({ "Medication" })
    private final List<Reference> associatedMedication;
    private final List<CodeableConcept> productType;
    private final List<Monograph> monograph;
    private final Markdown preparationInstruction;
    private final List<Cost> cost;
    @Summary
    private final List<MonitoringProgram> monitoringProgram;
    private final List<IndicationGuideline> indicationGuideline;
    private final List<MedicineClassification> medicineClassification;
    private final List<Packaging> packaging;
    @ReferenceTarget({ "ClinicalUseDefinition" })
    private final List<Reference> clinicalUseIssue;
    private final List<StorageGuideline> storageGuideline;
    private final List<Regulatory> regulatory;
    private final Definitional definitional;

    private MedicationKnowledge(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        code = builder.code;
        status = builder.status;
        author = builder.author;
        intendedJurisdiction = Collections.unmodifiableList(builder.intendedJurisdiction);
        name = Collections.unmodifiableList(builder.name);
        relatedMedicationKnowledge = Collections.unmodifiableList(builder.relatedMedicationKnowledge);
        associatedMedication = Collections.unmodifiableList(builder.associatedMedication);
        productType = Collections.unmodifiableList(builder.productType);
        monograph = Collections.unmodifiableList(builder.monograph);
        preparationInstruction = builder.preparationInstruction;
        cost = Collections.unmodifiableList(builder.cost);
        monitoringProgram = Collections.unmodifiableList(builder.monitoringProgram);
        indicationGuideline = Collections.unmodifiableList(builder.indicationGuideline);
        medicineClassification = Collections.unmodifiableList(builder.medicineClassification);
        packaging = Collections.unmodifiableList(builder.packaging);
        clinicalUseIssue = Collections.unmodifiableList(builder.clinicalUseIssue);
        storageGuideline = Collections.unmodifiableList(builder.storageGuideline);
        regulatory = Collections.unmodifiableList(builder.regulatory);
        definitional = builder.definitional;
    }

    /**
     * Business identifier for this medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A code that specifies this medication, or a textual description if no code is available. Usage note: This could be a 
     * standard medication code such as a code from RxNorm, SNOMED CT, IDMP etc. It could also be a national or local 
     * formulary code, optionally with translations to other code systems.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * A code to indicate if the medication referred to by this MedicationKnowledge is in active use within the drug database 
     * or inventory system. The status refers to the validity about the information of the medication and not to its 
     * medicinal properties.
     * 
     * @return
     *     An immutable object of type {@link MedicationKnowledgeStatus} that may be null.
     */
    public MedicationKnowledgeStatus getStatus() {
        return status;
    }

    /**
     * The creator or owner of the knowledge or information about the medication.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getAuthor() {
        return author;
    }

    /**
     * Lists the jurisdictions that this medication knowledge was written for.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getIntendedJurisdiction() {
        return intendedJurisdiction;
    }

    /**
     * All of the names for a medication, for example, the name(s) given to a medication in different countries. For example, 
     * acetaminophen and paracetamol or salbutamol and albuterol.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
     */
    public List<String> getName() {
        return name;
    }

    /**
     * Associated or related medications. For example, if the medication is a branded product (e.g. Crestor), this is the 
     * Therapeutic Moeity (e.g. Rosuvastatin) or if this is a generic medication (e.g. Rosuvastatin), this would link to a 
     * branded product (e.g. Crestor.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedMedicationKnowledge} that may be empty.
     */
    public List<RelatedMedicationKnowledge> getRelatedMedicationKnowledge() {
        return relatedMedicationKnowledge;
    }

    /**
     * Links to associated medications that could be prescribed, dispensed or administered.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAssociatedMedication() {
        return associatedMedication;
    }

    /**
     * Category of the medication or product (e.g. branded product, therapeutic moeity, generic product, innovator product, 
     * etc.).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getProductType() {
        return productType;
    }

    /**
     * Associated documentation about the medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Monograph} that may be empty.
     */
    public List<Monograph> getMonograph() {
        return monograph;
    }

    /**
     * The instructions for preparing the medication.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPreparationInstruction() {
        return preparationInstruction;
    }

    /**
     * The price of the medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Cost} that may be empty.
     */
    public List<Cost> getCost() {
        return cost;
    }

    /**
     * The program under which the medication is reviewed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link MonitoringProgram} that may be empty.
     */
    public List<MonitoringProgram> getMonitoringProgram() {
        return monitoringProgram;
    }

    /**
     * Guidelines or protocols that are applicable for the administration of the medication based on indication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link IndicationGuideline} that may be empty.
     */
    public List<IndicationGuideline> getIndicationGuideline() {
        return indicationGuideline;
    }

    /**
     * Categorization of the medication within a formulary or classification system.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link MedicineClassification} that may be empty.
     */
    public List<MedicineClassification> getMedicineClassification() {
        return medicineClassification;
    }

    /**
     * Information that only applies to packages (not products).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Packaging} that may be empty.
     */
    public List<Packaging> getPackaging() {
        return packaging;
    }

    /**
     * Potential clinical issue with or between medication(s) (for example, drug-drug interaction, drug-disease 
     * contraindication, drug-allergy interaction, etc.).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getClinicalUseIssue() {
        return clinicalUseIssue;
    }

    /**
     * Information on how the medication should be stored, for example, refrigeration temperatures and length of stability at 
     * a given temperature.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link StorageGuideline} that may be empty.
     */
    public List<StorageGuideline> getStorageGuideline() {
        return storageGuideline;
    }

    /**
     * Regulatory information about a medication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Regulatory} that may be empty.
     */
    public List<Regulatory> getRegulatory() {
        return regulatory;
    }

    /**
     * Along with the link to a Medicinal Product Definition resource, this information provides common definitional elements 
     * that are needed to understand the specific medication that is being described.
     * 
     * @return
     *     An immutable object of type {@link Definitional} that may be null.
     */
    public Definitional getDefinitional() {
        return definitional;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (code != null) || 
            (status != null) || 
            (author != null) || 
            !intendedJurisdiction.isEmpty() || 
            !name.isEmpty() || 
            !relatedMedicationKnowledge.isEmpty() || 
            !associatedMedication.isEmpty() || 
            !productType.isEmpty() || 
            !monograph.isEmpty() || 
            (preparationInstruction != null) || 
            !cost.isEmpty() || 
            !monitoringProgram.isEmpty() || 
            !indicationGuideline.isEmpty() || 
            !medicineClassification.isEmpty() || 
            !packaging.isEmpty() || 
            !clinicalUseIssue.isEmpty() || 
            !storageGuideline.isEmpty() || 
            !regulatory.isEmpty() || 
            (definitional != null);
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
                accept(code, "code", visitor);
                accept(status, "status", visitor);
                accept(author, "author", visitor);
                accept(intendedJurisdiction, "intendedJurisdiction", visitor, CodeableConcept.class);
                accept(name, "name", visitor, String.class);
                accept(relatedMedicationKnowledge, "relatedMedicationKnowledge", visitor, RelatedMedicationKnowledge.class);
                accept(associatedMedication, "associatedMedication", visitor, Reference.class);
                accept(productType, "productType", visitor, CodeableConcept.class);
                accept(monograph, "monograph", visitor, Monograph.class);
                accept(preparationInstruction, "preparationInstruction", visitor);
                accept(cost, "cost", visitor, Cost.class);
                accept(monitoringProgram, "monitoringProgram", visitor, MonitoringProgram.class);
                accept(indicationGuideline, "indicationGuideline", visitor, IndicationGuideline.class);
                accept(medicineClassification, "medicineClassification", visitor, MedicineClassification.class);
                accept(packaging, "packaging", visitor, Packaging.class);
                accept(clinicalUseIssue, "clinicalUseIssue", visitor, Reference.class);
                accept(storageGuideline, "storageGuideline", visitor, StorageGuideline.class);
                accept(regulatory, "regulatory", visitor, Regulatory.class);
                accept(definitional, "definitional", visitor);
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
        MedicationKnowledge other = (MedicationKnowledge) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(code, other.code) && 
            Objects.equals(status, other.status) && 
            Objects.equals(author, other.author) && 
            Objects.equals(intendedJurisdiction, other.intendedJurisdiction) && 
            Objects.equals(name, other.name) && 
            Objects.equals(relatedMedicationKnowledge, other.relatedMedicationKnowledge) && 
            Objects.equals(associatedMedication, other.associatedMedication) && 
            Objects.equals(productType, other.productType) && 
            Objects.equals(monograph, other.monograph) && 
            Objects.equals(preparationInstruction, other.preparationInstruction) && 
            Objects.equals(cost, other.cost) && 
            Objects.equals(monitoringProgram, other.monitoringProgram) && 
            Objects.equals(indicationGuideline, other.indicationGuideline) && 
            Objects.equals(medicineClassification, other.medicineClassification) && 
            Objects.equals(packaging, other.packaging) && 
            Objects.equals(clinicalUseIssue, other.clinicalUseIssue) && 
            Objects.equals(storageGuideline, other.storageGuideline) && 
            Objects.equals(regulatory, other.regulatory) && 
            Objects.equals(definitional, other.definitional);
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
                code, 
                status, 
                author, 
                intendedJurisdiction, 
                name, 
                relatedMedicationKnowledge, 
                associatedMedication, 
                productType, 
                monograph, 
                preparationInstruction, 
                cost, 
                monitoringProgram, 
                indicationGuideline, 
                medicineClassification, 
                packaging, 
                clinicalUseIssue, 
                storageGuideline, 
                regulatory, 
                definitional);
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
        private CodeableConcept code;
        private MedicationKnowledgeStatus status;
        private Reference author;
        private List<CodeableConcept> intendedJurisdiction = new ArrayList<>();
        private List<String> name = new ArrayList<>();
        private List<RelatedMedicationKnowledge> relatedMedicationKnowledge = new ArrayList<>();
        private List<Reference> associatedMedication = new ArrayList<>();
        private List<CodeableConcept> productType = new ArrayList<>();
        private List<Monograph> monograph = new ArrayList<>();
        private Markdown preparationInstruction;
        private List<Cost> cost = new ArrayList<>();
        private List<MonitoringProgram> monitoringProgram = new ArrayList<>();
        private List<IndicationGuideline> indicationGuideline = new ArrayList<>();
        private List<MedicineClassification> medicineClassification = new ArrayList<>();
        private List<Packaging> packaging = new ArrayList<>();
        private List<Reference> clinicalUseIssue = new ArrayList<>();
        private List<StorageGuideline> storageGuideline = new ArrayList<>();
        private List<Regulatory> regulatory = new ArrayList<>();
        private Definitional definitional;

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
         * Business identifier for this medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this medication
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
         * Business identifier for this medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for this medication
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
         * A code that specifies this medication, or a textual description if no code is available. Usage note: This could be a 
         * standard medication code such as a code from RxNorm, SNOMED CT, IDMP etc. It could also be a national or local 
         * formulary code, optionally with translations to other code systems.
         * 
         * @param code
         *     Code that identifies this medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * A code to indicate if the medication referred to by this MedicationKnowledge is in active use within the drug database 
         * or inventory system. The status refers to the validity about the information of the medication and not to its 
         * medicinal properties.
         * 
         * @param status
         *     active | entered-in-error | inactive
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MedicationKnowledgeStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The creator or owner of the knowledge or information about the medication.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param author
         *     Creator or owner of the knowledge or information about the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder author(Reference author) {
            this.author = author;
            return this;
        }

        /**
         * Lists the jurisdictions that this medication knowledge was written for.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param intendedJurisdiction
         *     Codes that identify the different jurisdictions for which the information of this resource was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intendedJurisdiction(CodeableConcept... intendedJurisdiction) {
            for (CodeableConcept value : intendedJurisdiction) {
                this.intendedJurisdiction.add(value);
            }
            return this;
        }

        /**
         * Lists the jurisdictions that this medication knowledge was written for.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param intendedJurisdiction
         *     Codes that identify the different jurisdictions for which the information of this resource was created
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder intendedJurisdiction(Collection<CodeableConcept> intendedJurisdiction) {
            this.intendedJurisdiction = new ArrayList<>(intendedJurisdiction);
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     A name associated with the medication being described
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #name(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder name(java.lang.String... name) {
            for (java.lang.String value : name) {
                this.name.add((value == null) ? null : String.of(value));
            }
            return this;
        }

        /**
         * All of the names for a medication, for example, the name(s) given to a medication in different countries. For example, 
         * acetaminophen and paracetamol or salbutamol and albuterol.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     A name associated with the medication being described
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String... name) {
            for (String value : name) {
                this.name.add(value);
            }
            return this;
        }

        /**
         * All of the names for a medication, for example, the name(s) given to a medication in different countries. For example, 
         * acetaminophen and paracetamol or salbutamol and albuterol.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     A name associated with the medication being described
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder name(Collection<String> name) {
            this.name = new ArrayList<>(name);
            return this;
        }

        /**
         * Associated or related medications. For example, if the medication is a branded product (e.g. Crestor), this is the 
         * Therapeutic Moeity (e.g. Rosuvastatin) or if this is a generic medication (e.g. Rosuvastatin), this would link to a 
         * branded product (e.g. Crestor.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedMedicationKnowledge
         *     Associated or related medication information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatedMedicationKnowledge(RelatedMedicationKnowledge... relatedMedicationKnowledge) {
            for (RelatedMedicationKnowledge value : relatedMedicationKnowledge) {
                this.relatedMedicationKnowledge.add(value);
            }
            return this;
        }

        /**
         * Associated or related medications. For example, if the medication is a branded product (e.g. Crestor), this is the 
         * Therapeutic Moeity (e.g. Rosuvastatin) or if this is a generic medication (e.g. Rosuvastatin), this would link to a 
         * branded product (e.g. Crestor.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedMedicationKnowledge
         *     Associated or related medication information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatedMedicationKnowledge(Collection<RelatedMedicationKnowledge> relatedMedicationKnowledge) {
            this.relatedMedicationKnowledge = new ArrayList<>(relatedMedicationKnowledge);
            return this;
        }

        /**
         * Links to associated medications that could be prescribed, dispensed or administered.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Medication}</li>
         * </ul>
         * 
         * @param associatedMedication
         *     The set of medication resources that are associated with this medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder associatedMedication(Reference... associatedMedication) {
            for (Reference value : associatedMedication) {
                this.associatedMedication.add(value);
            }
            return this;
        }

        /**
         * Links to associated medications that could be prescribed, dispensed or administered.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Medication}</li>
         * </ul>
         * 
         * @param associatedMedication
         *     The set of medication resources that are associated with this medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder associatedMedication(Collection<Reference> associatedMedication) {
            this.associatedMedication = new ArrayList<>(associatedMedication);
            return this;
        }

        /**
         * Category of the medication or product (e.g. branded product, therapeutic moeity, generic product, innovator product, 
         * etc.).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param productType
         *     Category of the medication or product
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productType(CodeableConcept... productType) {
            for (CodeableConcept value : productType) {
                this.productType.add(value);
            }
            return this;
        }

        /**
         * Category of the medication or product (e.g. branded product, therapeutic moeity, generic product, innovator product, 
         * etc.).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param productType
         *     Category of the medication or product
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder productType(Collection<CodeableConcept> productType) {
            this.productType = new ArrayList<>(productType);
            return this;
        }

        /**
         * Associated documentation about the medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monograph
         *     Associated documentation about the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder monograph(Monograph... monograph) {
            for (Monograph value : monograph) {
                this.monograph.add(value);
            }
            return this;
        }

        /**
         * Associated documentation about the medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monograph
         *     Associated documentation about the medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder monograph(Collection<Monograph> monograph) {
            this.monograph = new ArrayList<>(monograph);
            return this;
        }

        /**
         * The instructions for preparing the medication.
         * 
         * @param preparationInstruction
         *     The instructions for preparing the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preparationInstruction(Markdown preparationInstruction) {
            this.preparationInstruction = preparationInstruction;
            return this;
        }

        /**
         * The price of the medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param cost
         *     The pricing of the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder cost(Cost... cost) {
            for (Cost value : cost) {
                this.cost.add(value);
            }
            return this;
        }

        /**
         * The price of the medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param cost
         *     The pricing of the medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder cost(Collection<Cost> cost) {
            this.cost = new ArrayList<>(cost);
            return this;
        }

        /**
         * The program under which the medication is reviewed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monitoringProgram
         *     Program under which a medication is reviewed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder monitoringProgram(MonitoringProgram... monitoringProgram) {
            for (MonitoringProgram value : monitoringProgram) {
                this.monitoringProgram.add(value);
            }
            return this;
        }

        /**
         * The program under which the medication is reviewed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monitoringProgram
         *     Program under which a medication is reviewed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder monitoringProgram(Collection<MonitoringProgram> monitoringProgram) {
            this.monitoringProgram = new ArrayList<>(monitoringProgram);
            return this;
        }

        /**
         * Guidelines or protocols that are applicable for the administration of the medication based on indication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param indicationGuideline
         *     Guidelines or protocols for administration of the medication for an indication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder indicationGuideline(IndicationGuideline... indicationGuideline) {
            for (IndicationGuideline value : indicationGuideline) {
                this.indicationGuideline.add(value);
            }
            return this;
        }

        /**
         * Guidelines or protocols that are applicable for the administration of the medication based on indication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param indicationGuideline
         *     Guidelines or protocols for administration of the medication for an indication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder indicationGuideline(Collection<IndicationGuideline> indicationGuideline) {
            this.indicationGuideline = new ArrayList<>(indicationGuideline);
            return this;
        }

        /**
         * Categorization of the medication within a formulary or classification system.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param medicineClassification
         *     Categorization of the medication within a formulary or classification system
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder medicineClassification(MedicineClassification... medicineClassification) {
            for (MedicineClassification value : medicineClassification) {
                this.medicineClassification.add(value);
            }
            return this;
        }

        /**
         * Categorization of the medication within a formulary or classification system.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param medicineClassification
         *     Categorization of the medication within a formulary or classification system
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder medicineClassification(Collection<MedicineClassification> medicineClassification) {
            this.medicineClassification = new ArrayList<>(medicineClassification);
            return this;
        }

        /**
         * Information that only applies to packages (not products).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param packaging
         *     Details about packaged medications
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder packaging(Packaging... packaging) {
            for (Packaging value : packaging) {
                this.packaging.add(value);
            }
            return this;
        }

        /**
         * Information that only applies to packages (not products).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param packaging
         *     Details about packaged medications
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder packaging(Collection<Packaging> packaging) {
            this.packaging = new ArrayList<>(packaging);
            return this;
        }

        /**
         * Potential clinical issue with or between medication(s) (for example, drug-drug interaction, drug-disease 
         * contraindication, drug-allergy interaction, etc.).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ClinicalUseDefinition}</li>
         * </ul>
         * 
         * @param clinicalUseIssue
         *     Potential clinical issue with or between medication(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clinicalUseIssue(Reference... clinicalUseIssue) {
            for (Reference value : clinicalUseIssue) {
                this.clinicalUseIssue.add(value);
            }
            return this;
        }

        /**
         * Potential clinical issue with or between medication(s) (for example, drug-drug interaction, drug-disease 
         * contraindication, drug-allergy interaction, etc.).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ClinicalUseDefinition}</li>
         * </ul>
         * 
         * @param clinicalUseIssue
         *     Potential clinical issue with or between medication(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder clinicalUseIssue(Collection<Reference> clinicalUseIssue) {
            this.clinicalUseIssue = new ArrayList<>(clinicalUseIssue);
            return this;
        }

        /**
         * Information on how the medication should be stored, for example, refrigeration temperatures and length of stability at 
         * a given temperature.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param storageGuideline
         *     How the medication should be stored
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder storageGuideline(StorageGuideline... storageGuideline) {
            for (StorageGuideline value : storageGuideline) {
                this.storageGuideline.add(value);
            }
            return this;
        }

        /**
         * Information on how the medication should be stored, for example, refrigeration temperatures and length of stability at 
         * a given temperature.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param storageGuideline
         *     How the medication should be stored
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder storageGuideline(Collection<StorageGuideline> storageGuideline) {
            this.storageGuideline = new ArrayList<>(storageGuideline);
            return this;
        }

        /**
         * Regulatory information about a medication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatory
         *     Regulatory information about a medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder regulatory(Regulatory... regulatory) {
            for (Regulatory value : regulatory) {
                this.regulatory.add(value);
            }
            return this;
        }

        /**
         * Regulatory information about a medication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatory
         *     Regulatory information about a medication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder regulatory(Collection<Regulatory> regulatory) {
            this.regulatory = new ArrayList<>(regulatory);
            return this;
        }

        /**
         * Along with the link to a Medicinal Product Definition resource, this information provides common definitional elements 
         * that are needed to understand the specific medication that is being described.
         * 
         * @param definitional
         *     Minimal definition information about the medication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder definitional(Definitional definitional) {
            this.definitional = definitional;
            return this;
        }

        /**
         * Build the {@link MedicationKnowledge}
         * 
         * @return
         *     An immutable object of type {@link MedicationKnowledge}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MedicationKnowledge per the base specification
         */
        @Override
        public MedicationKnowledge build() {
            MedicationKnowledge medicationKnowledge = new MedicationKnowledge(this);
            if (validating) {
                validate(medicationKnowledge);
            }
            return medicationKnowledge;
        }

        protected void validate(MedicationKnowledge medicationKnowledge) {
            super.validate(medicationKnowledge);
            ValidationSupport.checkList(medicationKnowledge.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(medicationKnowledge.intendedJurisdiction, "intendedJurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(medicationKnowledge.name, "name", String.class);
            ValidationSupport.checkList(medicationKnowledge.relatedMedicationKnowledge, "relatedMedicationKnowledge", RelatedMedicationKnowledge.class);
            ValidationSupport.checkList(medicationKnowledge.associatedMedication, "associatedMedication", Reference.class);
            ValidationSupport.checkList(medicationKnowledge.productType, "productType", CodeableConcept.class);
            ValidationSupport.checkList(medicationKnowledge.monograph, "monograph", Monograph.class);
            ValidationSupport.checkList(medicationKnowledge.cost, "cost", Cost.class);
            ValidationSupport.checkList(medicationKnowledge.monitoringProgram, "monitoringProgram", MonitoringProgram.class);
            ValidationSupport.checkList(medicationKnowledge.indicationGuideline, "indicationGuideline", IndicationGuideline.class);
            ValidationSupport.checkList(medicationKnowledge.medicineClassification, "medicineClassification", MedicineClassification.class);
            ValidationSupport.checkList(medicationKnowledge.packaging, "packaging", Packaging.class);
            ValidationSupport.checkList(medicationKnowledge.clinicalUseIssue, "clinicalUseIssue", Reference.class);
            ValidationSupport.checkList(medicationKnowledge.storageGuideline, "storageGuideline", StorageGuideline.class);
            ValidationSupport.checkList(medicationKnowledge.regulatory, "regulatory", Regulatory.class);
            ValidationSupport.checkReferenceType(medicationKnowledge.author, "author", "Organization");
            ValidationSupport.checkReferenceType(medicationKnowledge.associatedMedication, "associatedMedication", "Medication");
            ValidationSupport.checkReferenceType(medicationKnowledge.clinicalUseIssue, "clinicalUseIssue", "ClinicalUseDefinition");
        }

        protected Builder from(MedicationKnowledge medicationKnowledge) {
            super.from(medicationKnowledge);
            identifier.addAll(medicationKnowledge.identifier);
            code = medicationKnowledge.code;
            status = medicationKnowledge.status;
            author = medicationKnowledge.author;
            intendedJurisdiction.addAll(medicationKnowledge.intendedJurisdiction);
            name.addAll(medicationKnowledge.name);
            relatedMedicationKnowledge.addAll(medicationKnowledge.relatedMedicationKnowledge);
            associatedMedication.addAll(medicationKnowledge.associatedMedication);
            productType.addAll(medicationKnowledge.productType);
            monograph.addAll(medicationKnowledge.monograph);
            preparationInstruction = medicationKnowledge.preparationInstruction;
            cost.addAll(medicationKnowledge.cost);
            monitoringProgram.addAll(medicationKnowledge.monitoringProgram);
            indicationGuideline.addAll(medicationKnowledge.indicationGuideline);
            medicineClassification.addAll(medicationKnowledge.medicineClassification);
            packaging.addAll(medicationKnowledge.packaging);
            clinicalUseIssue.addAll(medicationKnowledge.clinicalUseIssue);
            storageGuideline.addAll(medicationKnowledge.storageGuideline);
            regulatory.addAll(medicationKnowledge.regulatory);
            definitional = medicationKnowledge.definitional;
            return this;
        }
    }

    /**
     * Associated or related medications. For example, if the medication is a branded product (e.g. Crestor), this is the 
     * Therapeutic Moeity (e.g. Rosuvastatin) or if this is a generic medication (e.g. Rosuvastatin), this would link to a 
     * branded product (e.g. Crestor.
     */
    public static class RelatedMedicationKnowledge extends BackboneElement {
        @Required
        private final CodeableConcept type;
        @ReferenceTarget({ "MedicationKnowledge" })
        @Required
        private final List<Reference> reference;

        private RelatedMedicationKnowledge(Builder builder) {
            super(builder);
            type = builder.type;
            reference = Collections.unmodifiableList(builder.reference);
        }

        /**
         * The category of the associated medication knowledge reference.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Associated documentation about the associated medication knowledge.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that is non-empty.
         */
        public List<Reference> getReference() {
            return reference;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !reference.isEmpty();
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
                    accept(reference, "reference", visitor, Reference.class);
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
            RelatedMedicationKnowledge other = (RelatedMedicationKnowledge) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(reference, other.reference);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    reference);
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
            private List<Reference> reference = new ArrayList<>();

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
             * The category of the associated medication knowledge reference.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Category of medicationKnowledge
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Associated documentation about the associated medication knowledge.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link MedicationKnowledge}</li>
             * </ul>
             * 
             * @param reference
             *     Associated documentation about the associated medication knowledge
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Reference... reference) {
                for (Reference value : reference) {
                    this.reference.add(value);
                }
                return this;
            }

            /**
             * Associated documentation about the associated medication knowledge.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link MedicationKnowledge}</li>
             * </ul>
             * 
             * @param reference
             *     Associated documentation about the associated medication knowledge
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder reference(Collection<Reference> reference) {
                this.reference = new ArrayList<>(reference);
                return this;
            }

            /**
             * Build the {@link RelatedMedicationKnowledge}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>reference</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link RelatedMedicationKnowledge}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid RelatedMedicationKnowledge per the base specification
             */
            @Override
            public RelatedMedicationKnowledge build() {
                RelatedMedicationKnowledge relatedMedicationKnowledge = new RelatedMedicationKnowledge(this);
                if (validating) {
                    validate(relatedMedicationKnowledge);
                }
                return relatedMedicationKnowledge;
            }

            protected void validate(RelatedMedicationKnowledge relatedMedicationKnowledge) {
                super.validate(relatedMedicationKnowledge);
                ValidationSupport.requireNonNull(relatedMedicationKnowledge.type, "type");
                ValidationSupport.checkNonEmptyList(relatedMedicationKnowledge.reference, "reference", Reference.class);
                ValidationSupport.checkReferenceType(relatedMedicationKnowledge.reference, "reference", "MedicationKnowledge");
                ValidationSupport.requireValueOrChildren(relatedMedicationKnowledge);
            }

            protected Builder from(RelatedMedicationKnowledge relatedMedicationKnowledge) {
                super.from(relatedMedicationKnowledge);
                type = relatedMedicationKnowledge.type;
                reference.addAll(relatedMedicationKnowledge.reference);
                return this;
            }
        }
    }

    /**
     * Associated documentation about the medication.
     */
    public static class Monograph extends BackboneElement {
        private final CodeableConcept type;
        @ReferenceTarget({ "DocumentReference" })
        private final Reference source;

        private Monograph(Builder builder) {
            super(builder);
            type = builder.type;
            source = builder.source;
        }

        /**
         * The category of documentation about the medication. (e.g. professional monograph, patient education monograph).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Associated documentation about the medication.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSource() {
            return source;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (source != null);
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
                    accept(source, "source", visitor);
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
            Monograph other = (Monograph) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(source, other.source);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    source);
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
            private Reference source;

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
             * The category of documentation about the medication. (e.g. professional monograph, patient education monograph).
             * 
             * @param type
             *     The category of medication document
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Associated documentation about the medication.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * </ul>
             * 
             * @param source
             *     Associated documentation about the medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(Reference source) {
                this.source = source;
                return this;
            }

            /**
             * Build the {@link Monograph}
             * 
             * @return
             *     An immutable object of type {@link Monograph}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Monograph per the base specification
             */
            @Override
            public Monograph build() {
                Monograph monograph = new Monograph(this);
                if (validating) {
                    validate(monograph);
                }
                return monograph;
            }

            protected void validate(Monograph monograph) {
                super.validate(monograph);
                ValidationSupport.checkReferenceType(monograph.source, "source", "DocumentReference");
                ValidationSupport.requireValueOrChildren(monograph);
            }

            protected Builder from(Monograph monograph) {
                super.from(monograph);
                type = monograph.type;
                source = monograph.source;
                return this;
            }
        }
    }

    /**
     * The price of the medication.
     */
    public static class Cost extends BackboneElement {
        private final List<Period> effectiveDate;
        @Required
        private final CodeableConcept type;
        private final String source;
        @Choice({ Money.class, CodeableConcept.class })
        @Binding(
            bindingName = "MedicationCostCategory",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A coded concept defining the category of a medication.",
            valueSet = "http://hl7.org/fhir/ValueSet/medication-cost-category"
        )
        @Required
        private final Element cost;

        private Cost(Builder builder) {
            super(builder);
            effectiveDate = Collections.unmodifiableList(builder.effectiveDate);
            type = builder.type;
            source = builder.source;
            cost = builder.cost;
        }

        /**
         * The date range for which the cost information of the medication is effective.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Period} that may be empty.
         */
        public List<Period> getEffectiveDate() {
            return effectiveDate;
        }

        /**
         * The category of the cost information. For example, manufacturers' cost, patient cost, claim reimbursement cost, actual 
         * acquisition cost.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The source or owner that assigns the price to the medication.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getSource() {
            return source;
        }

        /**
         * The price or representation of the cost (for example, Band A, Band B or $, $$) of the medication.
         * 
         * @return
         *     An immutable object of type {@link Money} or {@link CodeableConcept} that is non-null.
         */
        public Element getCost() {
            return cost;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !effectiveDate.isEmpty() || 
                (type != null) || 
                (source != null) || 
                (cost != null);
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
                    accept(effectiveDate, "effectiveDate", visitor, Period.class);
                    accept(type, "type", visitor);
                    accept(source, "source", visitor);
                    accept(cost, "cost", visitor);
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
            Cost other = (Cost) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(effectiveDate, other.effectiveDate) && 
                Objects.equals(type, other.type) && 
                Objects.equals(source, other.source) && 
                Objects.equals(cost, other.cost);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    effectiveDate, 
                    type, 
                    source, 
                    cost);
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
            private List<Period> effectiveDate = new ArrayList<>();
            private CodeableConcept type;
            private String source;
            private Element cost;

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
             * The date range for which the cost information of the medication is effective.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param effectiveDate
             *     The date range for which the cost is effective
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder effectiveDate(Period... effectiveDate) {
                for (Period value : effectiveDate) {
                    this.effectiveDate.add(value);
                }
                return this;
            }

            /**
             * The date range for which the cost information of the medication is effective.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param effectiveDate
             *     The date range for which the cost is effective
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder effectiveDate(Collection<Period> effectiveDate) {
                this.effectiveDate = new ArrayList<>(effectiveDate);
                return this;
            }

            /**
             * The category of the cost information. For example, manufacturers' cost, patient cost, claim reimbursement cost, actual 
             * acquisition cost.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     The category of the cost information
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code source}.
             * 
             * @param source
             *     The source or owner for the price information
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #source(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder source(java.lang.String source) {
                this.source = (source == null) ? null : String.of(source);
                return this;
            }

            /**
             * The source or owner that assigns the price to the medication.
             * 
             * @param source
             *     The source or owner for the price information
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(String source) {
                this.source = source;
                return this;
            }

            /**
             * The price or representation of the cost (for example, Band A, Band B or $, $$) of the medication.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Money}</li>
             * <li>{@link CodeableConcept}</li>
             * </ul>
             * 
             * @param cost
             *     The price or category of the cost of the medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder cost(Element cost) {
                this.cost = cost;
                return this;
            }

            /**
             * Build the {@link Cost}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>cost</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Cost}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Cost per the base specification
             */
            @Override
            public Cost build() {
                Cost cost = new Cost(this);
                if (validating) {
                    validate(cost);
                }
                return cost;
            }

            protected void validate(Cost cost) {
                super.validate(cost);
                ValidationSupport.checkList(cost.effectiveDate, "effectiveDate", Period.class);
                ValidationSupport.requireNonNull(cost.type, "type");
                ValidationSupport.requireChoiceElement(cost.cost, "cost", Money.class, CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(cost);
            }

            protected Builder from(Cost cost) {
                super.from(cost);
                effectiveDate.addAll(cost.effectiveDate);
                type = cost.type;
                source = cost.source;
                this.cost = cost.cost;
                return this;
            }
        }
    }

    /**
     * The program under which the medication is reviewed.
     */
    public static class MonitoringProgram extends BackboneElement {
        private final CodeableConcept type;
        private final String name;

        private MonitoringProgram(Builder builder) {
            super(builder);
            type = builder.type;
            name = builder.name;
        }

        /**
         * Type of program under which the medication is monitored.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Name of the reviewing program.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (name != null);
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
                    accept(name, "name", visitor);
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
            MonitoringProgram other = (MonitoringProgram) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    name);
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
            private String name;

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
             * Type of program under which the medication is monitored.
             * 
             * @param type
             *     Type of program under which the medication is monitored
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Name of the reviewing program
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
             * Name of the reviewing program.
             * 
             * @param name
             *     Name of the reviewing program
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Build the {@link MonitoringProgram}
             * 
             * @return
             *     An immutable object of type {@link MonitoringProgram}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid MonitoringProgram per the base specification
             */
            @Override
            public MonitoringProgram build() {
                MonitoringProgram monitoringProgram = new MonitoringProgram(this);
                if (validating) {
                    validate(monitoringProgram);
                }
                return monitoringProgram;
            }

            protected void validate(MonitoringProgram monitoringProgram) {
                super.validate(monitoringProgram);
                ValidationSupport.requireValueOrChildren(monitoringProgram);
            }

            protected Builder from(MonitoringProgram monitoringProgram) {
                super.from(monitoringProgram);
                type = monitoringProgram.type;
                name = monitoringProgram.name;
                return this;
            }
        }
    }

    /**
     * Guidelines or protocols that are applicable for the administration of the medication based on indication.
     */
    public static class IndicationGuideline extends BackboneElement {
        private final List<CodeableReference> indication;
        private final List<DosingGuideline> dosingGuideline;

        private IndicationGuideline(Builder builder) {
            super(builder);
            indication = Collections.unmodifiableList(builder.indication);
            dosingGuideline = Collections.unmodifiableList(builder.dosingGuideline);
        }

        /**
         * Indication or reason for use of the medication that applies to the specific administration guideline.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getIndication() {
            return indication;
        }

        /**
         * The guidelines for the dosage of the medication for the indication.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link DosingGuideline} that may be empty.
         */
        public List<DosingGuideline> getDosingGuideline() {
            return dosingGuideline;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !indication.isEmpty() || 
                !dosingGuideline.isEmpty();
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
                    accept(indication, "indication", visitor, CodeableReference.class);
                    accept(dosingGuideline, "dosingGuideline", visitor, DosingGuideline.class);
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
            IndicationGuideline other = (IndicationGuideline) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(indication, other.indication) && 
                Objects.equals(dosingGuideline, other.dosingGuideline);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    indication, 
                    dosingGuideline);
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
            private List<CodeableReference> indication = new ArrayList<>();
            private List<DosingGuideline> dosingGuideline = new ArrayList<>();

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
             * Indication or reason for use of the medication that applies to the specific administration guideline.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param indication
             *     Indication for use that applies to the specific administration guideline
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder indication(CodeableReference... indication) {
                for (CodeableReference value : indication) {
                    this.indication.add(value);
                }
                return this;
            }

            /**
             * Indication or reason for use of the medication that applies to the specific administration guideline.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param indication
             *     Indication for use that applies to the specific administration guideline
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder indication(Collection<CodeableReference> indication) {
                this.indication = new ArrayList<>(indication);
                return this;
            }

            /**
             * The guidelines for the dosage of the medication for the indication.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dosingGuideline
             *     Guidelines for dosage of the medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dosingGuideline(DosingGuideline... dosingGuideline) {
                for (DosingGuideline value : dosingGuideline) {
                    this.dosingGuideline.add(value);
                }
                return this;
            }

            /**
             * The guidelines for the dosage of the medication for the indication.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param dosingGuideline
             *     Guidelines for dosage of the medication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder dosingGuideline(Collection<DosingGuideline> dosingGuideline) {
                this.dosingGuideline = new ArrayList<>(dosingGuideline);
                return this;
            }

            /**
             * Build the {@link IndicationGuideline}
             * 
             * @return
             *     An immutable object of type {@link IndicationGuideline}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid IndicationGuideline per the base specification
             */
            @Override
            public IndicationGuideline build() {
                IndicationGuideline indicationGuideline = new IndicationGuideline(this);
                if (validating) {
                    validate(indicationGuideline);
                }
                return indicationGuideline;
            }

            protected void validate(IndicationGuideline indicationGuideline) {
                super.validate(indicationGuideline);
                ValidationSupport.checkList(indicationGuideline.indication, "indication", CodeableReference.class);
                ValidationSupport.checkList(indicationGuideline.dosingGuideline, "dosingGuideline", DosingGuideline.class);
                ValidationSupport.requireValueOrChildren(indicationGuideline);
            }

            protected Builder from(IndicationGuideline indicationGuideline) {
                super.from(indicationGuideline);
                indication.addAll(indicationGuideline.indication);
                dosingGuideline.addAll(indicationGuideline.dosingGuideline);
                return this;
            }
        }

        /**
         * The guidelines for the dosage of the medication for the indication.
         */
        public static class DosingGuideline extends BackboneElement {
            private final CodeableConcept treatmentIntent;
            private final List<Dosage> dosage;
            private final CodeableConcept administrationTreatment;
            private final List<PatientCharacteristic> patientCharacteristic;

            private DosingGuideline(Builder builder) {
                super(builder);
                treatmentIntent = builder.treatmentIntent;
                dosage = Collections.unmodifiableList(builder.dosage);
                administrationTreatment = builder.administrationTreatment;
                patientCharacteristic = Collections.unmodifiableList(builder.patientCharacteristic);
            }

            /**
             * The overall intention of the treatment, for example, prophylactic, supporative, curative, etc.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getTreatmentIntent() {
                return treatmentIntent;
            }

            /**
             * Dosage for the medication for the specific guidelines.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Dosage} that may be empty.
             */
            public List<Dosage> getDosage() {
                return dosage;
            }

            /**
             * The type of the treatment that the guideline applies to, for example, long term therapy, first line treatment, etc.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getAdministrationTreatment() {
                return administrationTreatment;
            }

            /**
             * Characteristics of the patient that are relevant to the administration guidelines (for example, height, weight, 
             * gender, etc.).
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link PatientCharacteristic} that may be empty.
             */
            public List<PatientCharacteristic> getPatientCharacteristic() {
                return patientCharacteristic;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (treatmentIntent != null) || 
                    !dosage.isEmpty() || 
                    (administrationTreatment != null) || 
                    !patientCharacteristic.isEmpty();
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
                        accept(treatmentIntent, "treatmentIntent", visitor);
                        accept(dosage, "dosage", visitor, Dosage.class);
                        accept(administrationTreatment, "administrationTreatment", visitor);
                        accept(patientCharacteristic, "patientCharacteristic", visitor, PatientCharacteristic.class);
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
                DosingGuideline other = (DosingGuideline) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(treatmentIntent, other.treatmentIntent) && 
                    Objects.equals(dosage, other.dosage) && 
                    Objects.equals(administrationTreatment, other.administrationTreatment) && 
                    Objects.equals(patientCharacteristic, other.patientCharacteristic);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        treatmentIntent, 
                        dosage, 
                        administrationTreatment, 
                        patientCharacteristic);
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
                private CodeableConcept treatmentIntent;
                private List<Dosage> dosage = new ArrayList<>();
                private CodeableConcept administrationTreatment;
                private List<PatientCharacteristic> patientCharacteristic = new ArrayList<>();

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
                 * The overall intention of the treatment, for example, prophylactic, supporative, curative, etc.
                 * 
                 * @param treatmentIntent
                 *     Intention of the treatment
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder treatmentIntent(CodeableConcept treatmentIntent) {
                    this.treatmentIntent = treatmentIntent;
                    return this;
                }

                /**
                 * Dosage for the medication for the specific guidelines.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param dosage
                 *     Dosage for the medication for the specific guidelines
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder dosage(Dosage... dosage) {
                    for (Dosage value : dosage) {
                        this.dosage.add(value);
                    }
                    return this;
                }

                /**
                 * Dosage for the medication for the specific guidelines.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param dosage
                 *     Dosage for the medication for the specific guidelines
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder dosage(Collection<Dosage> dosage) {
                    this.dosage = new ArrayList<>(dosage);
                    return this;
                }

                /**
                 * The type of the treatment that the guideline applies to, for example, long term therapy, first line treatment, etc.
                 * 
                 * @param administrationTreatment
                 *     Type of treatment the guideline applies to
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder administrationTreatment(CodeableConcept administrationTreatment) {
                    this.administrationTreatment = administrationTreatment;
                    return this;
                }

                /**
                 * Characteristics of the patient that are relevant to the administration guidelines (for example, height, weight, 
                 * gender, etc.).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param patientCharacteristic
                 *     Characteristics of the patient that are relevant to the administration guidelines
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder patientCharacteristic(PatientCharacteristic... patientCharacteristic) {
                    for (PatientCharacteristic value : patientCharacteristic) {
                        this.patientCharacteristic.add(value);
                    }
                    return this;
                }

                /**
                 * Characteristics of the patient that are relevant to the administration guidelines (for example, height, weight, 
                 * gender, etc.).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param patientCharacteristic
                 *     Characteristics of the patient that are relevant to the administration guidelines
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder patientCharacteristic(Collection<PatientCharacteristic> patientCharacteristic) {
                    this.patientCharacteristic = new ArrayList<>(patientCharacteristic);
                    return this;
                }

                /**
                 * Build the {@link DosingGuideline}
                 * 
                 * @return
                 *     An immutable object of type {@link DosingGuideline}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid DosingGuideline per the base specification
                 */
                @Override
                public DosingGuideline build() {
                    DosingGuideline dosingGuideline = new DosingGuideline(this);
                    if (validating) {
                        validate(dosingGuideline);
                    }
                    return dosingGuideline;
                }

                protected void validate(DosingGuideline dosingGuideline) {
                    super.validate(dosingGuideline);
                    ValidationSupport.checkList(dosingGuideline.dosage, "dosage", Dosage.class);
                    ValidationSupport.checkList(dosingGuideline.patientCharacteristic, "patientCharacteristic", PatientCharacteristic.class);
                    ValidationSupport.requireValueOrChildren(dosingGuideline);
                }

                protected Builder from(DosingGuideline dosingGuideline) {
                    super.from(dosingGuideline);
                    treatmentIntent = dosingGuideline.treatmentIntent;
                    dosage.addAll(dosingGuideline.dosage);
                    administrationTreatment = dosingGuideline.administrationTreatment;
                    patientCharacteristic.addAll(dosingGuideline.patientCharacteristic);
                    return this;
                }
            }

            /**
             * Dosage for the medication for the specific guidelines.
             */
            public static class Dosage extends BackboneElement {
                @Required
                private final CodeableConcept type;
                @Required
                private final List<org.linuxforhealth.fhir.model.r5.type.Dosage> dosage;

                private Dosage(Builder builder) {
                    super(builder);
                    type = builder.type;
                    dosage = Collections.unmodifiableList(builder.dosage);
                }

                /**
                 * The type or category of dosage for a given medication (for example, prophylaxis, maintenance, therapeutic, etc.).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that is non-null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * Dosage for the medication for the specific guidelines.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Dosage} that is non-empty.
                 */
                public List<org.linuxforhealth.fhir.model.r5.type.Dosage> getDosage() {
                    return dosage;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        !dosage.isEmpty();
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
                            accept(dosage, "dosage", visitor, org.linuxforhealth.fhir.model.r5.type.Dosage.class);
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
                    Dosage other = (Dosage) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(dosage, other.dosage);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            type, 
                            dosage);
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
                    private List<org.linuxforhealth.fhir.model.r5.type.Dosage> dosage = new ArrayList<>();

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
                     * The type or category of dosage for a given medication (for example, prophylaxis, maintenance, therapeutic, etc.).
                     * 
                     * <p>This element is required.
                     * 
                     * @param type
                     *     Category of dosage for a medication
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * Dosage for the medication for the specific guidelines.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * <p>This element is required.
                     * 
                     * @param dosage
                     *     Dosage for the medication for the specific guidelines
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder dosage(org.linuxforhealth.fhir.model.r5.type.Dosage... dosage) {
                        for (org.linuxforhealth.fhir.model.r5.type.Dosage value : dosage) {
                            this.dosage.add(value);
                        }
                        return this;
                    }

                    /**
                     * Dosage for the medication for the specific guidelines.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * <p>This element is required.
                     * 
                     * @param dosage
                     *     Dosage for the medication for the specific guidelines
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder dosage(Collection<org.linuxforhealth.fhir.model.r5.type.Dosage> dosage) {
                        this.dosage = new ArrayList<>(dosage);
                        return this;
                    }

                    /**
                     * Build the {@link Dosage}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>type</li>
                     * <li>dosage</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Dosage}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Dosage per the base specification
                     */
                    @Override
                    public Dosage build() {
                        Dosage dosage = new Dosage(this);
                        if (validating) {
                            validate(dosage);
                        }
                        return dosage;
                    }

                    protected void validate(Dosage dosage) {
                        super.validate(dosage);
                        ValidationSupport.requireNonNull(dosage.type, "type");
                        ValidationSupport.checkNonEmptyList(dosage.dosage, "dosage", org.linuxforhealth.fhir.model.r5.type.Dosage.class);
                        ValidationSupport.requireValueOrChildren(dosage);
                    }

                    protected Builder from(Dosage dosage) {
                        super.from(dosage);
                        type = dosage.type;
                        this.dosage.addAll(dosage.dosage);
                        return this;
                    }
                }
            }

            /**
             * Characteristics of the patient that are relevant to the administration guidelines (for example, height, weight, 
             * gender, etc.).
             */
            public static class PatientCharacteristic extends BackboneElement {
                @Required
                private final CodeableConcept type;
                @Choice({ CodeableConcept.class, Quantity.class, Range.class })
                private final Element value;

                private PatientCharacteristic(Builder builder) {
                    super(builder);
                    type = builder.type;
                    value = builder.value;
                }

                /**
                 * The categorization of the specific characteristic that is relevant to the administration guideline (e.g. height, 
                 * weight, gender).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that is non-null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * The specific characteristic (e.g. height, weight, gender, etc.).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept}, {@link Quantity} or {@link Range} that may be null.
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
                    PatientCharacteristic other = (PatientCharacteristic) obj;
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
                     * The categorization of the specific characteristic that is relevant to the administration guideline (e.g. height, 
                     * weight, gender).
                     * 
                     * <p>This element is required.
                     * 
                     * @param type
                     *     Categorization of specific characteristic that is relevant to the administration guideline
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * The specific characteristic (e.g. height, weight, gender, etc.).
                     * 
                     * <p>This is a choice element with the following allowed types:
                     * <ul>
                     * <li>{@link CodeableConcept}</li>
                     * <li>{@link Quantity}</li>
                     * <li>{@link Range}</li>
                     * </ul>
                     * 
                     * @param value
                     *     The specific characteristic
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder value(Element value) {
                        this.value = value;
                        return this;
                    }

                    /**
                     * Build the {@link PatientCharacteristic}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>type</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link PatientCharacteristic}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid PatientCharacteristic per the base specification
                     */
                    @Override
                    public PatientCharacteristic build() {
                        PatientCharacteristic patientCharacteristic = new PatientCharacteristic(this);
                        if (validating) {
                            validate(patientCharacteristic);
                        }
                        return patientCharacteristic;
                    }

                    protected void validate(PatientCharacteristic patientCharacteristic) {
                        super.validate(patientCharacteristic);
                        ValidationSupport.requireNonNull(patientCharacteristic.type, "type");
                        ValidationSupport.choiceElement(patientCharacteristic.value, "value", CodeableConcept.class, Quantity.class, Range.class);
                        ValidationSupport.requireValueOrChildren(patientCharacteristic);
                    }

                    protected Builder from(PatientCharacteristic patientCharacteristic) {
                        super.from(patientCharacteristic);
                        type = patientCharacteristic.type;
                        value = patientCharacteristic.value;
                        return this;
                    }
                }
            }
        }
    }

    /**
     * Categorization of the medication within a formulary or classification system.
     */
    public static class MedicineClassification extends BackboneElement {
        @Required
        private final CodeableConcept type;
        @Choice({ String.class, Uri.class })
        private final Element source;
        private final List<CodeableConcept> classification;

        private MedicineClassification(Builder builder) {
            super(builder);
            type = builder.type;
            source = builder.source;
            classification = Collections.unmodifiableList(builder.classification);
        }

        /**
         * The type of category for the medication (for example, therapeutic classification, therapeutic sub-classification).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Either a textual source of the classification or a reference to an online source.
         * 
         * @return
         *     An immutable object of type {@link String} or {@link Uri} that may be null.
         */
        public Element getSource() {
            return source;
        }

        /**
         * Specific category assigned to the medication (e.g. anti-infective, anti-hypertensive, antibiotic, etc.).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getClassification() {
            return classification;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (source != null) || 
                !classification.isEmpty();
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
                    accept(source, "source", visitor);
                    accept(classification, "classification", visitor, CodeableConcept.class);
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
            MedicineClassification other = (MedicineClassification) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(source, other.source) && 
                Objects.equals(classification, other.classification);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    source, 
                    classification);
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
            private Element source;
            private List<CodeableConcept> classification = new ArrayList<>();

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
             * The type of category for the medication (for example, therapeutic classification, therapeutic sub-classification).
             * 
             * <p>This element is required.
             * 
             * @param type
             *     The type of category for the medication (for example, therapeutic classification, therapeutic sub-classification)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code source} with choice type String.
             * 
             * @param source
             *     The source of the classification
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #source(Element)
             */
            public Builder source(java.lang.String source) {
                this.source = (source == null) ? null : String.of(source);
                return this;
            }

            /**
             * Either a textual source of the classification or a reference to an online source.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link String}</li>
             * <li>{@link Uri}</li>
             * </ul>
             * 
             * @param source
             *     The source of the classification
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(Element source) {
                this.source = source;
                return this;
            }

            /**
             * Specific category assigned to the medication (e.g. anti-infective, anti-hypertensive, antibiotic, etc.).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classification
             *     Specific category assigned to the medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder classification(CodeableConcept... classification) {
                for (CodeableConcept value : classification) {
                    this.classification.add(value);
                }
                return this;
            }

            /**
             * Specific category assigned to the medication (e.g. anti-infective, anti-hypertensive, antibiotic, etc.).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classification
             *     Specific category assigned to the medication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder classification(Collection<CodeableConcept> classification) {
                this.classification = new ArrayList<>(classification);
                return this;
            }

            /**
             * Build the {@link MedicineClassification}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link MedicineClassification}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid MedicineClassification per the base specification
             */
            @Override
            public MedicineClassification build() {
                MedicineClassification medicineClassification = new MedicineClassification(this);
                if (validating) {
                    validate(medicineClassification);
                }
                return medicineClassification;
            }

            protected void validate(MedicineClassification medicineClassification) {
                super.validate(medicineClassification);
                ValidationSupport.requireNonNull(medicineClassification.type, "type");
                ValidationSupport.choiceElement(medicineClassification.source, "source", String.class, Uri.class);
                ValidationSupport.checkList(medicineClassification.classification, "classification", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(medicineClassification);
            }

            protected Builder from(MedicineClassification medicineClassification) {
                super.from(medicineClassification);
                type = medicineClassification.type;
                source = medicineClassification.source;
                classification.addAll(medicineClassification.classification);
                return this;
            }
        }
    }

    /**
     * Information that only applies to packages (not products).
     */
    public static class Packaging extends BackboneElement {
        private final List<MedicationKnowledge.Cost> cost;
        @ReferenceTarget({ "PackagedProductDefinition" })
        private final Reference packagedProduct;

        private Packaging(Builder builder) {
            super(builder);
            cost = Collections.unmodifiableList(builder.cost);
            packagedProduct = builder.packagedProduct;
        }

        /**
         * The cost of the packaged medication.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Cost} that may be empty.
         */
        public List<MedicationKnowledge.Cost> getCost() {
            return cost;
        }

        /**
         * A reference to a PackagedProductDefinition that provides the details of the product that is in the packaging and is 
         * being priced.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getPackagedProduct() {
            return packagedProduct;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !cost.isEmpty() || 
                (packagedProduct != null);
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
                    accept(cost, "cost", visitor, MedicationKnowledge.Cost.class);
                    accept(packagedProduct, "packagedProduct", visitor);
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
            Packaging other = (Packaging) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(cost, other.cost) && 
                Objects.equals(packagedProduct, other.packagedProduct);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    cost, 
                    packagedProduct);
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
            private List<MedicationKnowledge.Cost> cost = new ArrayList<>();
            private Reference packagedProduct;

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
             * The cost of the packaged medication.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param cost
             *     Cost of the packaged medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder cost(MedicationKnowledge.Cost... cost) {
                for (MedicationKnowledge.Cost value : cost) {
                    this.cost.add(value);
                }
                return this;
            }

            /**
             * The cost of the packaged medication.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param cost
             *     Cost of the packaged medication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder cost(Collection<MedicationKnowledge.Cost> cost) {
                this.cost = new ArrayList<>(cost);
                return this;
            }

            /**
             * A reference to a PackagedProductDefinition that provides the details of the product that is in the packaging and is 
             * being priced.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link PackagedProductDefinition}</li>
             * </ul>
             * 
             * @param packagedProduct
             *     The packaged medication that is being priced
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder packagedProduct(Reference packagedProduct) {
                this.packagedProduct = packagedProduct;
                return this;
            }

            /**
             * Build the {@link Packaging}
             * 
             * @return
             *     An immutable object of type {@link Packaging}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Packaging per the base specification
             */
            @Override
            public Packaging build() {
                Packaging packaging = new Packaging(this);
                if (validating) {
                    validate(packaging);
                }
                return packaging;
            }

            protected void validate(Packaging packaging) {
                super.validate(packaging);
                ValidationSupport.checkList(packaging.cost, "cost", MedicationKnowledge.Cost.class);
                ValidationSupport.checkReferenceType(packaging.packagedProduct, "packagedProduct", "PackagedProductDefinition");
                ValidationSupport.requireValueOrChildren(packaging);
            }

            protected Builder from(Packaging packaging) {
                super.from(packaging);
                cost.addAll(packaging.cost);
                packagedProduct = packaging.packagedProduct;
                return this;
            }
        }
    }

    /**
     * Information on how the medication should be stored, for example, refrigeration temperatures and length of stability at 
     * a given temperature.
     */
    public static class StorageGuideline extends BackboneElement {
        private final Uri reference;
        private final List<Annotation> note;
        private final Duration stabilityDuration;
        private final List<EnvironmentalSetting> environmentalSetting;

        private StorageGuideline(Builder builder) {
            super(builder);
            reference = builder.reference;
            note = Collections.unmodifiableList(builder.note);
            stabilityDuration = builder.stabilityDuration;
            environmentalSetting = Collections.unmodifiableList(builder.environmentalSetting);
        }

        /**
         * Reference to additional information about the storage guidelines.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getReference() {
            return reference;
        }

        /**
         * Additional notes about the storage.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
         */
        public List<Annotation> getNote() {
            return note;
        }

        /**
         * Duration that the medication remains stable if the environmentalSetting is respected.
         * 
         * @return
         *     An immutable object of type {@link Duration} that may be null.
         */
        public Duration getStabilityDuration() {
            return stabilityDuration;
        }

        /**
         * Describes a setting/value on the environment for the adequate storage of the medication and other substances. 
         * Environment settings may involve temperature, humidity, or exposure to light.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link EnvironmentalSetting} that may be empty.
         */
        public List<EnvironmentalSetting> getEnvironmentalSetting() {
            return environmentalSetting;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (reference != null) || 
                !note.isEmpty() || 
                (stabilityDuration != null) || 
                !environmentalSetting.isEmpty();
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
                    accept(reference, "reference", visitor);
                    accept(note, "note", visitor, Annotation.class);
                    accept(stabilityDuration, "stabilityDuration", visitor);
                    accept(environmentalSetting, "environmentalSetting", visitor, EnvironmentalSetting.class);
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
            StorageGuideline other = (StorageGuideline) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(reference, other.reference) && 
                Objects.equals(note, other.note) && 
                Objects.equals(stabilityDuration, other.stabilityDuration) && 
                Objects.equals(environmentalSetting, other.environmentalSetting);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    reference, 
                    note, 
                    stabilityDuration, 
                    environmentalSetting);
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
            private Uri reference;
            private List<Annotation> note = new ArrayList<>();
            private Duration stabilityDuration;
            private List<EnvironmentalSetting> environmentalSetting = new ArrayList<>();

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
             * Reference to additional information about the storage guidelines.
             * 
             * @param reference
             *     Reference to additional information
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Uri reference) {
                this.reference = reference;
                return this;
            }

            /**
             * Additional notes about the storage.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Additional storage notes
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder note(Annotation... note) {
                for (Annotation value : note) {
                    this.note.add(value);
                }
                return this;
            }

            /**
             * Additional notes about the storage.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Additional storage notes
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder note(Collection<Annotation> note) {
                this.note = new ArrayList<>(note);
                return this;
            }

            /**
             * Duration that the medication remains stable if the environmentalSetting is respected.
             * 
             * @param stabilityDuration
             *     Duration remains stable
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder stabilityDuration(Duration stabilityDuration) {
                this.stabilityDuration = stabilityDuration;
                return this;
            }

            /**
             * Describes a setting/value on the environment for the adequate storage of the medication and other substances. 
             * Environment settings may involve temperature, humidity, or exposure to light.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param environmentalSetting
             *     Setting or value of environment for adequate storage
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder environmentalSetting(EnvironmentalSetting... environmentalSetting) {
                for (EnvironmentalSetting value : environmentalSetting) {
                    this.environmentalSetting.add(value);
                }
                return this;
            }

            /**
             * Describes a setting/value on the environment for the adequate storage of the medication and other substances. 
             * Environment settings may involve temperature, humidity, or exposure to light.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param environmentalSetting
             *     Setting or value of environment for adequate storage
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder environmentalSetting(Collection<EnvironmentalSetting> environmentalSetting) {
                this.environmentalSetting = new ArrayList<>(environmentalSetting);
                return this;
            }

            /**
             * Build the {@link StorageGuideline}
             * 
             * @return
             *     An immutable object of type {@link StorageGuideline}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid StorageGuideline per the base specification
             */
            @Override
            public StorageGuideline build() {
                StorageGuideline storageGuideline = new StorageGuideline(this);
                if (validating) {
                    validate(storageGuideline);
                }
                return storageGuideline;
            }

            protected void validate(StorageGuideline storageGuideline) {
                super.validate(storageGuideline);
                ValidationSupport.checkList(storageGuideline.note, "note", Annotation.class);
                ValidationSupport.checkList(storageGuideline.environmentalSetting, "environmentalSetting", EnvironmentalSetting.class);
                ValidationSupport.requireValueOrChildren(storageGuideline);
            }

            protected Builder from(StorageGuideline storageGuideline) {
                super.from(storageGuideline);
                reference = storageGuideline.reference;
                note.addAll(storageGuideline.note);
                stabilityDuration = storageGuideline.stabilityDuration;
                environmentalSetting.addAll(storageGuideline.environmentalSetting);
                return this;
            }
        }

        /**
         * Describes a setting/value on the environment for the adequate storage of the medication and other substances. 
         * Environment settings may involve temperature, humidity, or exposure to light.
         */
        public static class EnvironmentalSetting extends BackboneElement {
            @Required
            private final CodeableConcept type;
            @Choice({ Quantity.class, Range.class, CodeableConcept.class })
            @Required
            private final Element value;

            private EnvironmentalSetting(Builder builder) {
                super(builder);
                type = builder.type;
                value = builder.value;
            }

            /**
             * Identifies the category or type of setting (e.g., type of location, temperature, humidity).
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Value associated to the setting. E.g., 40° – 50°F for temperature.
             * 
             * @return
             *     An immutable object of type {@link Quantity}, {@link Range} or {@link CodeableConcept} that is non-null.
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
                EnvironmentalSetting other = (EnvironmentalSetting) obj;
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
                 * Identifies the category or type of setting (e.g., type of location, temperature, humidity).
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     Categorization of the setting
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Value associated to the setting. E.g., 40° – 50°F for temperature.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Quantity}</li>
                 * <li>{@link Range}</li>
                 * <li>{@link CodeableConcept}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Value of the setting
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link EnvironmentalSetting}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link EnvironmentalSetting}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid EnvironmentalSetting per the base specification
                 */
                @Override
                public EnvironmentalSetting build() {
                    EnvironmentalSetting environmentalSetting = new EnvironmentalSetting(this);
                    if (validating) {
                        validate(environmentalSetting);
                    }
                    return environmentalSetting;
                }

                protected void validate(EnvironmentalSetting environmentalSetting) {
                    super.validate(environmentalSetting);
                    ValidationSupport.requireNonNull(environmentalSetting.type, "type");
                    ValidationSupport.requireChoiceElement(environmentalSetting.value, "value", Quantity.class, Range.class, CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(environmentalSetting);
                }

                protected Builder from(EnvironmentalSetting environmentalSetting) {
                    super.from(environmentalSetting);
                    type = environmentalSetting.type;
                    value = environmentalSetting.value;
                    return this;
                }
            }
        }
    }

    /**
     * Regulatory information about a medication.
     */
    public static class Regulatory extends BackboneElement {
        @ReferenceTarget({ "Organization" })
        @Required
        private final Reference regulatoryAuthority;
        private final List<Substitution> substitution;
        private final List<CodeableConcept> schedule;
        private final MaxDispense maxDispense;

        private Regulatory(Builder builder) {
            super(builder);
            regulatoryAuthority = builder.regulatoryAuthority;
            substitution = Collections.unmodifiableList(builder.substitution);
            schedule = Collections.unmodifiableList(builder.schedule);
            maxDispense = builder.maxDispense;
        }

        /**
         * The authority that is specifying the regulations.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getRegulatoryAuthority() {
            return regulatoryAuthority;
        }

        /**
         * Specifies if changes are allowed when dispensing a medication from a regulatory perspective.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Substitution} that may be empty.
         */
        public List<Substitution> getSubstitution() {
            return substitution;
        }

        /**
         * Specifies the schedule of a medication in jurisdiction.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getSchedule() {
            return schedule;
        }

        /**
         * The maximum number of units of the medication that can be dispensed in a period.
         * 
         * @return
         *     An immutable object of type {@link MaxDispense} that may be null.
         */
        public MaxDispense getMaxDispense() {
            return maxDispense;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (regulatoryAuthority != null) || 
                !substitution.isEmpty() || 
                !schedule.isEmpty() || 
                (maxDispense != null);
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
                    accept(regulatoryAuthority, "regulatoryAuthority", visitor);
                    accept(substitution, "substitution", visitor, Substitution.class);
                    accept(schedule, "schedule", visitor, CodeableConcept.class);
                    accept(maxDispense, "maxDispense", visitor);
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
            Regulatory other = (Regulatory) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(regulatoryAuthority, other.regulatoryAuthority) && 
                Objects.equals(substitution, other.substitution) && 
                Objects.equals(schedule, other.schedule) && 
                Objects.equals(maxDispense, other.maxDispense);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    regulatoryAuthority, 
                    substitution, 
                    schedule, 
                    maxDispense);
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
            private Reference regulatoryAuthority;
            private List<Substitution> substitution = new ArrayList<>();
            private List<CodeableConcept> schedule = new ArrayList<>();
            private MaxDispense maxDispense;

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
             * The authority that is specifying the regulations.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param regulatoryAuthority
             *     Specifies the authority of the regulation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder regulatoryAuthority(Reference regulatoryAuthority) {
                this.regulatoryAuthority = regulatoryAuthority;
                return this;
            }

            /**
             * Specifies if changes are allowed when dispensing a medication from a regulatory perspective.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param substitution
             *     Specifies if changes are allowed when dispensing a medication from a regulatory perspective
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder substitution(Substitution... substitution) {
                for (Substitution value : substitution) {
                    this.substitution.add(value);
                }
                return this;
            }

            /**
             * Specifies if changes are allowed when dispensing a medication from a regulatory perspective.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param substitution
             *     Specifies if changes are allowed when dispensing a medication from a regulatory perspective
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder substitution(Collection<Substitution> substitution) {
                this.substitution = new ArrayList<>(substitution);
                return this;
            }

            /**
             * Specifies the schedule of a medication in jurisdiction.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param schedule
             *     Specifies the schedule of a medication in jurisdiction
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder schedule(CodeableConcept... schedule) {
                for (CodeableConcept value : schedule) {
                    this.schedule.add(value);
                }
                return this;
            }

            /**
             * Specifies the schedule of a medication in jurisdiction.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param schedule
             *     Specifies the schedule of a medication in jurisdiction
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder schedule(Collection<CodeableConcept> schedule) {
                this.schedule = new ArrayList<>(schedule);
                return this;
            }

            /**
             * The maximum number of units of the medication that can be dispensed in a period.
             * 
             * @param maxDispense
             *     The maximum number of units of the medication that can be dispensed in a period
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder maxDispense(MaxDispense maxDispense) {
                this.maxDispense = maxDispense;
                return this;
            }

            /**
             * Build the {@link Regulatory}
             * 
             * <p>Required elements:
             * <ul>
             * <li>regulatoryAuthority</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Regulatory}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Regulatory per the base specification
             */
            @Override
            public Regulatory build() {
                Regulatory regulatory = new Regulatory(this);
                if (validating) {
                    validate(regulatory);
                }
                return regulatory;
            }

            protected void validate(Regulatory regulatory) {
                super.validate(regulatory);
                ValidationSupport.requireNonNull(regulatory.regulatoryAuthority, "regulatoryAuthority");
                ValidationSupport.checkList(regulatory.substitution, "substitution", Substitution.class);
                ValidationSupport.checkList(regulatory.schedule, "schedule", CodeableConcept.class);
                ValidationSupport.checkReferenceType(regulatory.regulatoryAuthority, "regulatoryAuthority", "Organization");
                ValidationSupport.requireValueOrChildren(regulatory);
            }

            protected Builder from(Regulatory regulatory) {
                super.from(regulatory);
                regulatoryAuthority = regulatory.regulatoryAuthority;
                substitution.addAll(regulatory.substitution);
                schedule.addAll(regulatory.schedule);
                maxDispense = regulatory.maxDispense;
                return this;
            }
        }

        /**
         * Specifies if changes are allowed when dispensing a medication from a regulatory perspective.
         */
        public static class Substitution extends BackboneElement {
            @Required
            private final CodeableConcept type;
            @Required
            private final Boolean allowed;

            private Substitution(Builder builder) {
                super(builder);
                type = builder.type;
                allowed = builder.allowed;
            }

            /**
             * Specifies the type of substitution allowed.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Specifies if regulation allows for changes in the medication when dispensing.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that is non-null.
             */
            public Boolean getAllowed() {
                return allowed;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (allowed != null);
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
                        accept(allowed, "allowed", visitor);
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
                Substitution other = (Substitution) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(allowed, other.allowed);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        allowed);
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
                private Boolean allowed;

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
                 * Specifies the type of substitution allowed.
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     Specifies the type of substitution allowed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Convenience method for setting {@code allowed}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param allowed
                 *     Specifies if regulation allows for changes in the medication when dispensing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #allowed(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder allowed(java.lang.Boolean allowed) {
                    this.allowed = (allowed == null) ? null : Boolean.of(allowed);
                    return this;
                }

                /**
                 * Specifies if regulation allows for changes in the medication when dispensing.
                 * 
                 * <p>This element is required.
                 * 
                 * @param allowed
                 *     Specifies if regulation allows for changes in the medication when dispensing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder allowed(Boolean allowed) {
                    this.allowed = allowed;
                    return this;
                }

                /**
                 * Build the {@link Substitution}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * <li>allowed</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Substitution}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Substitution per the base specification
                 */
                @Override
                public Substitution build() {
                    Substitution substitution = new Substitution(this);
                    if (validating) {
                        validate(substitution);
                    }
                    return substitution;
                }

                protected void validate(Substitution substitution) {
                    super.validate(substitution);
                    ValidationSupport.requireNonNull(substitution.type, "type");
                    ValidationSupport.requireNonNull(substitution.allowed, "allowed");
                    ValidationSupport.requireValueOrChildren(substitution);
                }

                protected Builder from(Substitution substitution) {
                    super.from(substitution);
                    type = substitution.type;
                    allowed = substitution.allowed;
                    return this;
                }
            }
        }

        /**
         * The maximum number of units of the medication that can be dispensed in a period.
         */
        public static class MaxDispense extends BackboneElement {
            @Required
            private final SimpleQuantity quantity;
            private final Duration period;

            private MaxDispense(Builder builder) {
                super(builder);
                quantity = builder.quantity;
                period = builder.period;
            }

            /**
             * The maximum number of units of the medication that can be dispensed.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that is non-null.
             */
            public SimpleQuantity getQuantity() {
                return quantity;
            }

            /**
             * The period that applies to the maximum number of units.
             * 
             * @return
             *     An immutable object of type {@link Duration} that may be null.
             */
            public Duration getPeriod() {
                return period;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (quantity != null) || 
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
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(quantity, "quantity", visitor);
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
                MaxDispense other = (MaxDispense) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(period, other.period);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        quantity, 
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

            public static class Builder extends BackboneElement.Builder {
                private SimpleQuantity quantity;
                private Duration period;

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
                 * The maximum number of units of the medication that can be dispensed.
                 * 
                 * <p>This element is required.
                 * 
                 * @param quantity
                 *     The maximum number of units of the medication that can be dispensed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(SimpleQuantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * The period that applies to the maximum number of units.
                 * 
                 * @param period
                 *     The period that applies to the maximum number of units
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder period(Duration period) {
                    this.period = period;
                    return this;
                }

                /**
                 * Build the {@link MaxDispense}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>quantity</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link MaxDispense}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid MaxDispense per the base specification
                 */
                @Override
                public MaxDispense build() {
                    MaxDispense maxDispense = new MaxDispense(this);
                    if (validating) {
                        validate(maxDispense);
                    }
                    return maxDispense;
                }

                protected void validate(MaxDispense maxDispense) {
                    super.validate(maxDispense);
                    ValidationSupport.requireNonNull(maxDispense.quantity, "quantity");
                    ValidationSupport.requireValueOrChildren(maxDispense);
                }

                protected Builder from(MaxDispense maxDispense) {
                    super.from(maxDispense);
                    quantity = maxDispense.quantity;
                    period = maxDispense.period;
                    return this;
                }
            }
        }
    }

    /**
     * Along with the link to a Medicinal Product Definition resource, this information provides common definitional elements 
     * that are needed to understand the specific medication that is being described.
     */
    public static class Definitional extends BackboneElement {
        @ReferenceTarget({ "MedicinalProductDefinition" })
        private final List<Reference> definition;
        @Binding(
            bindingName = "MedicationForm",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/medication-form-codes"
        )
        private final CodeableConcept doseForm;
        @Binding(
            bindingName = "MedicationRoute",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/route-codes"
        )
        private final List<CodeableConcept> intendedRoute;
        @Summary
        private final List<Ingredient> ingredient;
        private final List<DrugCharacteristic> drugCharacteristic;

        private Definitional(Builder builder) {
            super(builder);
            definition = Collections.unmodifiableList(builder.definition);
            doseForm = builder.doseForm;
            intendedRoute = Collections.unmodifiableList(builder.intendedRoute);
            ingredient = Collections.unmodifiableList(builder.ingredient);
            drugCharacteristic = Collections.unmodifiableList(builder.drugCharacteristic);
        }

        /**
         * Associated definitions for this medication.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getDefinition() {
            return definition;
        }

        /**
         * Describes the form of the item. Powder; tablets; capsule.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getDoseForm() {
            return doseForm;
        }

        /**
         * The intended or approved route of administration.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getIntendedRoute() {
            return intendedRoute;
        }

        /**
         * Identifies a particular constituent of interest in the product.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Ingredient} that may be empty.
         */
        public List<Ingredient> getIngredient() {
            return ingredient;
        }

        /**
         * Specifies descriptive properties of the medicine, such as color, shape, imprints, etc.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link DrugCharacteristic} that may be empty.
         */
        public List<DrugCharacteristic> getDrugCharacteristic() {
            return drugCharacteristic;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !definition.isEmpty() || 
                (doseForm != null) || 
                !intendedRoute.isEmpty() || 
                !ingredient.isEmpty() || 
                !drugCharacteristic.isEmpty();
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
                    accept(definition, "definition", visitor, Reference.class);
                    accept(doseForm, "doseForm", visitor);
                    accept(intendedRoute, "intendedRoute", visitor, CodeableConcept.class);
                    accept(ingredient, "ingredient", visitor, Ingredient.class);
                    accept(drugCharacteristic, "drugCharacteristic", visitor, DrugCharacteristic.class);
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
            Definitional other = (Definitional) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(definition, other.definition) && 
                Objects.equals(doseForm, other.doseForm) && 
                Objects.equals(intendedRoute, other.intendedRoute) && 
                Objects.equals(ingredient, other.ingredient) && 
                Objects.equals(drugCharacteristic, other.drugCharacteristic);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    definition, 
                    doseForm, 
                    intendedRoute, 
                    ingredient, 
                    drugCharacteristic);
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
            private List<Reference> definition = new ArrayList<>();
            private CodeableConcept doseForm;
            private List<CodeableConcept> intendedRoute = new ArrayList<>();
            private List<Ingredient> ingredient = new ArrayList<>();
            private List<DrugCharacteristic> drugCharacteristic = new ArrayList<>();

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
             * Associated definitions for this medication.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link MedicinalProductDefinition}</li>
             * </ul>
             * 
             * @param definition
             *     Definitional resources that provide more information about this medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definition(Reference... definition) {
                for (Reference value : definition) {
                    this.definition.add(value);
                }
                return this;
            }

            /**
             * Associated definitions for this medication.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link MedicinalProductDefinition}</li>
             * </ul>
             * 
             * @param definition
             *     Definitional resources that provide more information about this medication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder definition(Collection<Reference> definition) {
                this.definition = new ArrayList<>(definition);
                return this;
            }

            /**
             * Describes the form of the item. Powder; tablets; capsule.
             * 
             * @param doseForm
             *     powder | tablets | capsule +
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder doseForm(CodeableConcept doseForm) {
                this.doseForm = doseForm;
                return this;
            }

            /**
             * The intended or approved route of administration.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param intendedRoute
             *     The intended or approved route of administration
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder intendedRoute(CodeableConcept... intendedRoute) {
                for (CodeableConcept value : intendedRoute) {
                    this.intendedRoute.add(value);
                }
                return this;
            }

            /**
             * The intended or approved route of administration.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param intendedRoute
             *     The intended or approved route of administration
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder intendedRoute(Collection<CodeableConcept> intendedRoute) {
                this.intendedRoute = new ArrayList<>(intendedRoute);
                return this;
            }

            /**
             * Identifies a particular constituent of interest in the product.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param ingredient
             *     Active or inactive ingredient
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder ingredient(Ingredient... ingredient) {
                for (Ingredient value : ingredient) {
                    this.ingredient.add(value);
                }
                return this;
            }

            /**
             * Identifies a particular constituent of interest in the product.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param ingredient
             *     Active or inactive ingredient
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder ingredient(Collection<Ingredient> ingredient) {
                this.ingredient = new ArrayList<>(ingredient);
                return this;
            }

            /**
             * Specifies descriptive properties of the medicine, such as color, shape, imprints, etc.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param drugCharacteristic
             *     Specifies descriptive properties of the medicine
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder drugCharacteristic(DrugCharacteristic... drugCharacteristic) {
                for (DrugCharacteristic value : drugCharacteristic) {
                    this.drugCharacteristic.add(value);
                }
                return this;
            }

            /**
             * Specifies descriptive properties of the medicine, such as color, shape, imprints, etc.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param drugCharacteristic
             *     Specifies descriptive properties of the medicine
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder drugCharacteristic(Collection<DrugCharacteristic> drugCharacteristic) {
                this.drugCharacteristic = new ArrayList<>(drugCharacteristic);
                return this;
            }

            /**
             * Build the {@link Definitional}
             * 
             * @return
             *     An immutable object of type {@link Definitional}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Definitional per the base specification
             */
            @Override
            public Definitional build() {
                Definitional definitional = new Definitional(this);
                if (validating) {
                    validate(definitional);
                }
                return definitional;
            }

            protected void validate(Definitional definitional) {
                super.validate(definitional);
                ValidationSupport.checkList(definitional.definition, "definition", Reference.class);
                ValidationSupport.checkList(definitional.intendedRoute, "intendedRoute", CodeableConcept.class);
                ValidationSupport.checkList(definitional.ingredient, "ingredient", Ingredient.class);
                ValidationSupport.checkList(definitional.drugCharacteristic, "drugCharacteristic", DrugCharacteristic.class);
                ValidationSupport.checkReferenceType(definitional.definition, "definition", "MedicinalProductDefinition");
                ValidationSupport.requireValueOrChildren(definitional);
            }

            protected Builder from(Definitional definitional) {
                super.from(definitional);
                definition.addAll(definitional.definition);
                doseForm = definitional.doseForm;
                intendedRoute.addAll(definitional.intendedRoute);
                ingredient.addAll(definitional.ingredient);
                drugCharacteristic.addAll(definitional.drugCharacteristic);
                return this;
            }
        }

        /**
         * Identifies a particular constituent of interest in the product.
         */
        public static class Ingredient extends BackboneElement {
            @Summary
            @Required
            private final CodeableReference item;
            @Binding(
                bindingName = "MedicationIngredientIsActive",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://terminology.hl7.org/ValueSet/v3-RoleClassIngredientEntity"
            )
            private final CodeableConcept type;
            @Choice({ Ratio.class, CodeableConcept.class, Quantity.class })
            @Binding(
                bindingName = "MedicationIngredientStrength",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/medication-ingredientstrength"
            )
            private final Element strength;

            private Ingredient(Builder builder) {
                super(builder);
                item = builder.item;
                type = builder.type;
                strength = builder.strength;
            }

            /**
             * A reference to the resource that provides information about the ingredient.
             * 
             * @return
             *     An immutable object of type {@link CodeableReference} that is non-null.
             */
            public CodeableReference getItem() {
                return item;
            }

            /**
             * Indication of whether this ingredient affects the therapeutic action of the drug.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Specifies how many (or how much) of the items there are in this Medication. For example, 250 mg per tablet. This is 
             * expressed as a ratio where the numerator is 250mg and the denominator is 1 tablet but can also be expressed a quantity 
             * when the denominator is assumed to be 1 tablet.
             * 
             * @return
             *     An immutable object of type {@link Ratio}, {@link CodeableConcept} or {@link Quantity} that may be null.
             */
            public Element getStrength() {
                return strength;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (item != null) || 
                    (type != null) || 
                    (strength != null);
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
                        accept(item, "item", visitor);
                        accept(type, "type", visitor);
                        accept(strength, "strength", visitor);
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
                Ingredient other = (Ingredient) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(item, other.item) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(strength, other.strength);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        item, 
                        type, 
                        strength);
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
                private CodeableReference item;
                private CodeableConcept type;
                private Element strength;

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
                 * A reference to the resource that provides information about the ingredient.
                 * 
                 * <p>This element is required.
                 * 
                 * @param item
                 *     Substances contained in the medication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder item(CodeableReference item) {
                    this.item = item;
                    return this;
                }

                /**
                 * Indication of whether this ingredient affects the therapeutic action of the drug.
                 * 
                 * @param type
                 *     A code that defines the type of ingredient, active, base, etc
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Specifies how many (or how much) of the items there are in this Medication. For example, 250 mg per tablet. This is 
                 * expressed as a ratio where the numerator is 250mg and the denominator is 1 tablet but can also be expressed a quantity 
                 * when the denominator is assumed to be 1 tablet.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Ratio}</li>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link Quantity}</li>
                 * </ul>
                 * 
                 * @param strength
                 *     Quantity of ingredient present
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder strength(Element strength) {
                    this.strength = strength;
                    return this;
                }

                /**
                 * Build the {@link Ingredient}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>item</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Ingredient}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Ingredient per the base specification
                 */
                @Override
                public Ingredient build() {
                    Ingredient ingredient = new Ingredient(this);
                    if (validating) {
                        validate(ingredient);
                    }
                    return ingredient;
                }

                protected void validate(Ingredient ingredient) {
                    super.validate(ingredient);
                    ValidationSupport.requireNonNull(ingredient.item, "item");
                    ValidationSupport.choiceElement(ingredient.strength, "strength", Ratio.class, CodeableConcept.class, Quantity.class);
                    ValidationSupport.requireValueOrChildren(ingredient);
                }

                protected Builder from(Ingredient ingredient) {
                    super.from(ingredient);
                    item = ingredient.item;
                    type = ingredient.type;
                    strength = ingredient.strength;
                    return this;
                }
            }
        }

        /**
         * Specifies descriptive properties of the medicine, such as color, shape, imprints, etc.
         */
        public static class DrugCharacteristic extends BackboneElement {
            @Binding(
                bindingName = "MedicationCharacteristic",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/medicationknowledge-characteristic"
            )
            private final CodeableConcept type;
            @Choice({ CodeableConcept.class, String.class, SimpleQuantity.class, Base64Binary.class, Attachment.class })
            private final Element value;

            private DrugCharacteristic(Builder builder) {
                super(builder);
                type = builder.type;
                value = builder.value;
            }

            /**
             * A code specifying which characteristic of the medicine is being described (for example, colour, shape, imprint).
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Description of the characteristic.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept}, {@link String}, {@link SimpleQuantity}, {@link Base64Binary} or 
             *     {@link Attachment} that may be null.
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
                DrugCharacteristic other = (DrugCharacteristic) obj;
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
                 * A code specifying which characteristic of the medicine is being described (for example, colour, shape, imprint).
                 * 
                 * @param type
                 *     Code specifying the type of characteristic of medication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type String.
                 * 
                 * @param value
                 *     Description of the characteristic
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
                 * Description of the characteristic.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link String}</li>
                 * <li>{@link SimpleQuantity}</li>
                 * <li>{@link Base64Binary}</li>
                 * <li>{@link Attachment}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Description of the characteristic
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link DrugCharacteristic}
                 * 
                 * @return
                 *     An immutable object of type {@link DrugCharacteristic}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid DrugCharacteristic per the base specification
                 */
                @Override
                public DrugCharacteristic build() {
                    DrugCharacteristic drugCharacteristic = new DrugCharacteristic(this);
                    if (validating) {
                        validate(drugCharacteristic);
                    }
                    return drugCharacteristic;
                }

                protected void validate(DrugCharacteristic drugCharacteristic) {
                    super.validate(drugCharacteristic);
                    ValidationSupport.choiceElement(drugCharacteristic.value, "value", CodeableConcept.class, String.class, SimpleQuantity.class, Base64Binary.class, Attachment.class);
                    ValidationSupport.requireValueOrChildren(drugCharacteristic);
                }

                protected Builder from(DrugCharacteristic drugCharacteristic) {
                    super.from(drugCharacteristic);
                    type = drugCharacteristic.type;
                    value = drugCharacteristic.value;
                    return this;
                }
            }
        }
    }
}
