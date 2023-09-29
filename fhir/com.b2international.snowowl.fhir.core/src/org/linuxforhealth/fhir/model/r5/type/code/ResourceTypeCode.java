/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.core.ResourceType;
import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/fhir-types")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ResourceTypeCode extends Code {
    public static final ResourceTypeCode ACCOUNT = ResourceTypeCode.builder().value(ResourceType.ACCOUNT).build();

    public static final ResourceTypeCode ACTIVITY_DEFINITION = ResourceTypeCode.builder().value(ResourceType.ACTIVITY_DEFINITION).build();

    public static final ResourceTypeCode ACTOR_DEFINITION = ResourceTypeCode.builder().value(ResourceType.ACTOR_DEFINITION).build();

    public static final ResourceTypeCode ADMINISTRABLE_PRODUCT_DEFINITION = ResourceTypeCode.builder().value(ResourceType.ADMINISTRABLE_PRODUCT_DEFINITION).build();

    public static final ResourceTypeCode ADVERSE_EVENT = ResourceTypeCode.builder().value(ResourceType.ADVERSE_EVENT).build();

    public static final ResourceTypeCode ALLERGY_INTOLERANCE = ResourceTypeCode.builder().value(ResourceType.ALLERGY_INTOLERANCE).build();

    public static final ResourceTypeCode APPOINTMENT = ResourceTypeCode.builder().value(ResourceType.APPOINTMENT).build();

    public static final ResourceTypeCode APPOINTMENT_RESPONSE = ResourceTypeCode.builder().value(ResourceType.APPOINTMENT_RESPONSE).build();

    public static final ResourceTypeCode ARTIFACT_ASSESSMENT = ResourceTypeCode.builder().value(ResourceType.ARTIFACT_ASSESSMENT).build();

    public static final ResourceTypeCode AUDIT_EVENT = ResourceTypeCode.builder().value(ResourceType.AUDIT_EVENT).build();

    public static final ResourceTypeCode BASIC = ResourceTypeCode.builder().value(ResourceType.BASIC).build();

    public static final ResourceTypeCode BINARY = ResourceTypeCode.builder().value(ResourceType.BINARY).build();

    public static final ResourceTypeCode BIOLOGICALLY_DERIVED_PRODUCT = ResourceTypeCode.builder().value(ResourceType.BIOLOGICALLY_DERIVED_PRODUCT).build();

    public static final ResourceTypeCode BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE = ResourceTypeCode.builder().value(ResourceType.BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE).build();

    public static final ResourceTypeCode BODY_STRUCTURE = ResourceTypeCode.builder().value(ResourceType.BODY_STRUCTURE).build();

    public static final ResourceTypeCode BUNDLE = ResourceTypeCode.builder().value(ResourceType.BUNDLE).build();

    public static final ResourceTypeCode CAPABILITY_STATEMENT = ResourceTypeCode.builder().value(ResourceType.CAPABILITY_STATEMENT).build();

    public static final ResourceTypeCode CARE_PLAN = ResourceTypeCode.builder().value(ResourceType.CARE_PLAN).build();

    public static final ResourceTypeCode CARE_TEAM = ResourceTypeCode.builder().value(ResourceType.CARE_TEAM).build();

    public static final ResourceTypeCode CHARGE_ITEM = ResourceTypeCode.builder().value(ResourceType.CHARGE_ITEM).build();

    public static final ResourceTypeCode CHARGE_ITEM_DEFINITION = ResourceTypeCode.builder().value(ResourceType.CHARGE_ITEM_DEFINITION).build();

    public static final ResourceTypeCode CITATION = ResourceTypeCode.builder().value(ResourceType.CITATION).build();

    public static final ResourceTypeCode CLAIM = ResourceTypeCode.builder().value(ResourceType.CLAIM).build();

    public static final ResourceTypeCode CLAIM_RESPONSE = ResourceTypeCode.builder().value(ResourceType.CLAIM_RESPONSE).build();

    public static final ResourceTypeCode CLINICAL_IMPRESSION = ResourceTypeCode.builder().value(ResourceType.CLINICAL_IMPRESSION).build();

    public static final ResourceTypeCode CLINICAL_USE_DEFINITION = ResourceTypeCode.builder().value(ResourceType.CLINICAL_USE_DEFINITION).build();

    public static final ResourceTypeCode CODE_SYSTEM = ResourceTypeCode.builder().value(ResourceType.CODE_SYSTEM).build();

    public static final ResourceTypeCode COMMUNICATION = ResourceTypeCode.builder().value(ResourceType.COMMUNICATION).build();

    public static final ResourceTypeCode COMMUNICATION_REQUEST = ResourceTypeCode.builder().value(ResourceType.COMMUNICATION_REQUEST).build();

    public static final ResourceTypeCode COMPARTMENT_DEFINITION = ResourceTypeCode.builder().value(ResourceType.COMPARTMENT_DEFINITION).build();

    public static final ResourceTypeCode COMPOSITION = ResourceTypeCode.builder().value(ResourceType.COMPOSITION).build();

    public static final ResourceTypeCode CONCEPT_MAP = ResourceTypeCode.builder().value(ResourceType.CONCEPT_MAP).build();

    public static final ResourceTypeCode CONDITION = ResourceTypeCode.builder().value(ResourceType.CONDITION).build();

    public static final ResourceTypeCode CONDITION_DEFINITION = ResourceTypeCode.builder().value(ResourceType.CONDITION_DEFINITION).build();

    public static final ResourceTypeCode CONSENT = ResourceTypeCode.builder().value(ResourceType.CONSENT).build();

    public static final ResourceTypeCode CONTRACT = ResourceTypeCode.builder().value(ResourceType.CONTRACT).build();

    public static final ResourceTypeCode COVERAGE = ResourceTypeCode.builder().value(ResourceType.COVERAGE).build();

    public static final ResourceTypeCode COVERAGE_ELIGIBILITY_REQUEST = ResourceTypeCode.builder().value(ResourceType.COVERAGE_ELIGIBILITY_REQUEST).build();

    public static final ResourceTypeCode COVERAGE_ELIGIBILITY_RESPONSE = ResourceTypeCode.builder().value(ResourceType.COVERAGE_ELIGIBILITY_RESPONSE).build();

    public static final ResourceTypeCode DETECTED_ISSUE = ResourceTypeCode.builder().value(ResourceType.DETECTED_ISSUE).build();

    public static final ResourceTypeCode DEVICE = ResourceTypeCode.builder().value(ResourceType.DEVICE).build();

    public static final ResourceTypeCode DEVICE_ASSOCIATION = ResourceTypeCode.builder().value(ResourceType.DEVICE_ASSOCIATION).build();

    public static final ResourceTypeCode DEVICE_DEFINITION = ResourceTypeCode.builder().value(ResourceType.DEVICE_DEFINITION).build();

    public static final ResourceTypeCode DEVICE_DISPENSE = ResourceTypeCode.builder().value(ResourceType.DEVICE_DISPENSE).build();

    public static final ResourceTypeCode DEVICE_METRIC = ResourceTypeCode.builder().value(ResourceType.DEVICE_METRIC).build();

    public static final ResourceTypeCode DEVICE_REQUEST = ResourceTypeCode.builder().value(ResourceType.DEVICE_REQUEST).build();

    public static final ResourceTypeCode DEVICE_USAGE = ResourceTypeCode.builder().value(ResourceType.DEVICE_USAGE).build();

    public static final ResourceTypeCode DIAGNOSTIC_REPORT = ResourceTypeCode.builder().value(ResourceType.DIAGNOSTIC_REPORT).build();

    public static final ResourceTypeCode DOCUMENT_REFERENCE = ResourceTypeCode.builder().value(ResourceType.DOCUMENT_REFERENCE).build();

    public static final ResourceTypeCode ENCOUNTER = ResourceTypeCode.builder().value(ResourceType.ENCOUNTER).build();

    public static final ResourceTypeCode ENCOUNTER_HISTORY = ResourceTypeCode.builder().value(ResourceType.ENCOUNTER_HISTORY).build();

    public static final ResourceTypeCode ENDPOINT = ResourceTypeCode.builder().value(ResourceType.ENDPOINT).build();

    public static final ResourceTypeCode ENROLLMENT_REQUEST = ResourceTypeCode.builder().value(ResourceType.ENROLLMENT_REQUEST).build();

    public static final ResourceTypeCode ENROLLMENT_RESPONSE = ResourceTypeCode.builder().value(ResourceType.ENROLLMENT_RESPONSE).build();

    public static final ResourceTypeCode EPISODE_OF_CARE = ResourceTypeCode.builder().value(ResourceType.EPISODE_OF_CARE).build();

    public static final ResourceTypeCode EVENT_DEFINITION = ResourceTypeCode.builder().value(ResourceType.EVENT_DEFINITION).build();

    public static final ResourceTypeCode EVIDENCE = ResourceTypeCode.builder().value(ResourceType.EVIDENCE).build();

    public static final ResourceTypeCode EVIDENCE_REPORT = ResourceTypeCode.builder().value(ResourceType.EVIDENCE_REPORT).build();

    public static final ResourceTypeCode EVIDENCE_VARIABLE = ResourceTypeCode.builder().value(ResourceType.EVIDENCE_VARIABLE).build();

    public static final ResourceTypeCode EXAMPLE_SCENARIO = ResourceTypeCode.builder().value(ResourceType.EXAMPLE_SCENARIO).build();

    public static final ResourceTypeCode EXPLANATION_OF_BENEFIT = ResourceTypeCode.builder().value(ResourceType.EXPLANATION_OF_BENEFIT).build();

    public static final ResourceTypeCode FAMILY_MEMBER_HISTORY = ResourceTypeCode.builder().value(ResourceType.FAMILY_MEMBER_HISTORY).build();

    public static final ResourceTypeCode FLAG = ResourceTypeCode.builder().value(ResourceType.FLAG).build();

    public static final ResourceTypeCode FORMULARY_ITEM = ResourceTypeCode.builder().value(ResourceType.FORMULARY_ITEM).build();

    public static final ResourceTypeCode GENOMIC_STUDY = ResourceTypeCode.builder().value(ResourceType.GENOMIC_STUDY).build();

    public static final ResourceTypeCode GOAL = ResourceTypeCode.builder().value(ResourceType.GOAL).build();

    public static final ResourceTypeCode GRAPH_DEFINITION = ResourceTypeCode.builder().value(ResourceType.GRAPH_DEFINITION).build();

    public static final ResourceTypeCode GROUP = ResourceTypeCode.builder().value(ResourceType.GROUP).build();

    public static final ResourceTypeCode GUIDANCE_RESPONSE = ResourceTypeCode.builder().value(ResourceType.GUIDANCE_RESPONSE).build();

    public static final ResourceTypeCode HEALTHCARE_SERVICE = ResourceTypeCode.builder().value(ResourceType.HEALTHCARE_SERVICE).build();

    public static final ResourceTypeCode IMAGING_SELECTION = ResourceTypeCode.builder().value(ResourceType.IMAGING_SELECTION).build();

    public static final ResourceTypeCode IMAGING_STUDY = ResourceTypeCode.builder().value(ResourceType.IMAGING_STUDY).build();

    public static final ResourceTypeCode IMMUNIZATION = ResourceTypeCode.builder().value(ResourceType.IMMUNIZATION).build();

    public static final ResourceTypeCode IMMUNIZATION_EVALUATION = ResourceTypeCode.builder().value(ResourceType.IMMUNIZATION_EVALUATION).build();

    public static final ResourceTypeCode IMMUNIZATION_RECOMMENDATION = ResourceTypeCode.builder().value(ResourceType.IMMUNIZATION_RECOMMENDATION).build();

    public static final ResourceTypeCode IMPLEMENTATION_GUIDE = ResourceTypeCode.builder().value(ResourceType.IMPLEMENTATION_GUIDE).build();

    public static final ResourceTypeCode INGREDIENT = ResourceTypeCode.builder().value(ResourceType.INGREDIENT).build();

    public static final ResourceTypeCode INSURANCE_PLAN = ResourceTypeCode.builder().value(ResourceType.INSURANCE_PLAN).build();

    public static final ResourceTypeCode INVENTORY_ITEM = ResourceTypeCode.builder().value(ResourceType.INVENTORY_ITEM).build();

    public static final ResourceTypeCode INVENTORY_REPORT = ResourceTypeCode.builder().value(ResourceType.INVENTORY_REPORT).build();

    public static final ResourceTypeCode INVOICE = ResourceTypeCode.builder().value(ResourceType.INVOICE).build();

    public static final ResourceTypeCode LIBRARY = ResourceTypeCode.builder().value(ResourceType.LIBRARY).build();

    public static final ResourceTypeCode LINKAGE = ResourceTypeCode.builder().value(ResourceType.LINKAGE).build();

    public static final ResourceTypeCode LIST = ResourceTypeCode.builder().value(ResourceType.LIST).build();

    public static final ResourceTypeCode LOCATION = ResourceTypeCode.builder().value(ResourceType.LOCATION).build();

    public static final ResourceTypeCode MANUFACTURED_ITEM_DEFINITION = ResourceTypeCode.builder().value(ResourceType.MANUFACTURED_ITEM_DEFINITION).build();

    public static final ResourceTypeCode MEASURE = ResourceTypeCode.builder().value(ResourceType.MEASURE).build();

    public static final ResourceTypeCode MEASURE_REPORT = ResourceTypeCode.builder().value(ResourceType.MEASURE_REPORT).build();

    public static final ResourceTypeCode MEDICATION = ResourceTypeCode.builder().value(ResourceType.MEDICATION).build();

    public static final ResourceTypeCode MEDICATION_ADMINISTRATION = ResourceTypeCode.builder().value(ResourceType.MEDICATION_ADMINISTRATION).build();

    public static final ResourceTypeCode MEDICATION_DISPENSE = ResourceTypeCode.builder().value(ResourceType.MEDICATION_DISPENSE).build();

    public static final ResourceTypeCode MEDICATION_KNOWLEDGE = ResourceTypeCode.builder().value(ResourceType.MEDICATION_KNOWLEDGE).build();

    public static final ResourceTypeCode MEDICATION_REQUEST = ResourceTypeCode.builder().value(ResourceType.MEDICATION_REQUEST).build();

    public static final ResourceTypeCode MEDICATION_STATEMENT = ResourceTypeCode.builder().value(ResourceType.MEDICATION_STATEMENT).build();

    public static final ResourceTypeCode MEDICINAL_PRODUCT_DEFINITION = ResourceTypeCode.builder().value(ResourceType.MEDICINAL_PRODUCT_DEFINITION).build();

    public static final ResourceTypeCode MESSAGE_DEFINITION = ResourceTypeCode.builder().value(ResourceType.MESSAGE_DEFINITION).build();

    public static final ResourceTypeCode MESSAGE_HEADER = ResourceTypeCode.builder().value(ResourceType.MESSAGE_HEADER).build();

    public static final ResourceTypeCode MOLECULAR_SEQUENCE = ResourceTypeCode.builder().value(ResourceType.MOLECULAR_SEQUENCE).build();

    public static final ResourceTypeCode NAMING_SYSTEM = ResourceTypeCode.builder().value(ResourceType.NAMING_SYSTEM).build();

    public static final ResourceTypeCode NUTRITION_INTAKE = ResourceTypeCode.builder().value(ResourceType.NUTRITION_INTAKE).build();

    public static final ResourceTypeCode NUTRITION_ORDER = ResourceTypeCode.builder().value(ResourceType.NUTRITION_ORDER).build();

    public static final ResourceTypeCode NUTRITION_PRODUCT = ResourceTypeCode.builder().value(ResourceType.NUTRITION_PRODUCT).build();

    public static final ResourceTypeCode OBSERVATION = ResourceTypeCode.builder().value(ResourceType.OBSERVATION).build();

    public static final ResourceTypeCode OBSERVATION_DEFINITION = ResourceTypeCode.builder().value(ResourceType.OBSERVATION_DEFINITION).build();

    public static final ResourceTypeCode OPERATION_DEFINITION = ResourceTypeCode.builder().value(ResourceType.OPERATION_DEFINITION).build();

    public static final ResourceTypeCode OPERATION_OUTCOME = ResourceTypeCode.builder().value(ResourceType.OPERATION_OUTCOME).build();

    public static final ResourceTypeCode ORGANIZATION = ResourceTypeCode.builder().value(ResourceType.ORGANIZATION).build();

    public static final ResourceTypeCode ORGANIZATION_AFFILIATION = ResourceTypeCode.builder().value(ResourceType.ORGANIZATION_AFFILIATION).build();

    public static final ResourceTypeCode PACKAGED_PRODUCT_DEFINITION = ResourceTypeCode.builder().value(ResourceType.PACKAGED_PRODUCT_DEFINITION).build();

    public static final ResourceTypeCode PARAMETERS = ResourceTypeCode.builder().value(ResourceType.PARAMETERS).build();

    public static final ResourceTypeCode PATIENT = ResourceTypeCode.builder().value(ResourceType.PATIENT).build();

    public static final ResourceTypeCode PAYMENT_NOTICE = ResourceTypeCode.builder().value(ResourceType.PAYMENT_NOTICE).build();

    public static final ResourceTypeCode PAYMENT_RECONCILIATION = ResourceTypeCode.builder().value(ResourceType.PAYMENT_RECONCILIATION).build();

    public static final ResourceTypeCode PERMISSION = ResourceTypeCode.builder().value(ResourceType.PERMISSION).build();

    public static final ResourceTypeCode PERSON = ResourceTypeCode.builder().value(ResourceType.PERSON).build();

    public static final ResourceTypeCode PLAN_DEFINITION = ResourceTypeCode.builder().value(ResourceType.PLAN_DEFINITION).build();

    public static final ResourceTypeCode PRACTITIONER = ResourceTypeCode.builder().value(ResourceType.PRACTITIONER).build();

    public static final ResourceTypeCode PRACTITIONER_ROLE = ResourceTypeCode.builder().value(ResourceType.PRACTITIONER_ROLE).build();

    public static final ResourceTypeCode PROCEDURE = ResourceTypeCode.builder().value(ResourceType.PROCEDURE).build();

    public static final ResourceTypeCode PROVENANCE = ResourceTypeCode.builder().value(ResourceType.PROVENANCE).build();

    public static final ResourceTypeCode QUESTIONNAIRE = ResourceTypeCode.builder().value(ResourceType.QUESTIONNAIRE).build();

    public static final ResourceTypeCode QUESTIONNAIRE_RESPONSE = ResourceTypeCode.builder().value(ResourceType.QUESTIONNAIRE_RESPONSE).build();

    public static final ResourceTypeCode REGULATED_AUTHORIZATION = ResourceTypeCode.builder().value(ResourceType.REGULATED_AUTHORIZATION).build();

    public static final ResourceTypeCode RELATED_PERSON = ResourceTypeCode.builder().value(ResourceType.RELATED_PERSON).build();

    public static final ResourceTypeCode REQUEST_ORCHESTRATION = ResourceTypeCode.builder().value(ResourceType.REQUEST_ORCHESTRATION).build();

    public static final ResourceTypeCode REQUIREMENTS = ResourceTypeCode.builder().value(ResourceType.REQUIREMENTS).build();

    public static final ResourceTypeCode RESEARCH_STUDY = ResourceTypeCode.builder().value(ResourceType.RESEARCH_STUDY).build();

    public static final ResourceTypeCode RESEARCH_SUBJECT = ResourceTypeCode.builder().value(ResourceType.RESEARCH_SUBJECT).build();

    public static final ResourceTypeCode RISK_ASSESSMENT = ResourceTypeCode.builder().value(ResourceType.RISK_ASSESSMENT).build();

    public static final ResourceTypeCode SCHEDULE = ResourceTypeCode.builder().value(ResourceType.SCHEDULE).build();

    public static final ResourceTypeCode SEARCH_PARAMETER = ResourceTypeCode.builder().value(ResourceType.SEARCH_PARAMETER).build();

    public static final ResourceTypeCode SERVICE_REQUEST = ResourceTypeCode.builder().value(ResourceType.SERVICE_REQUEST).build();

    public static final ResourceTypeCode SLOT = ResourceTypeCode.builder().value(ResourceType.SLOT).build();

    public static final ResourceTypeCode SPECIMEN = ResourceTypeCode.builder().value(ResourceType.SPECIMEN).build();

    public static final ResourceTypeCode SPECIMEN_DEFINITION = ResourceTypeCode.builder().value(ResourceType.SPECIMEN_DEFINITION).build();

    public static final ResourceTypeCode STRUCTURE_DEFINITION = ResourceTypeCode.builder().value(ResourceType.STRUCTURE_DEFINITION).build();

    public static final ResourceTypeCode STRUCTURE_MAP = ResourceTypeCode.builder().value(ResourceType.STRUCTURE_MAP).build();

    public static final ResourceTypeCode SUBSCRIPTION = ResourceTypeCode.builder().value(ResourceType.SUBSCRIPTION).build();

    public static final ResourceTypeCode SUBSCRIPTION_STATUS = ResourceTypeCode.builder().value(ResourceType.SUBSCRIPTION_STATUS).build();

    public static final ResourceTypeCode SUBSCRIPTION_TOPIC = ResourceTypeCode.builder().value(ResourceType.SUBSCRIPTION_TOPIC).build();

    public static final ResourceTypeCode SUBSTANCE = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE).build();

    public static final ResourceTypeCode SUBSTANCE_DEFINITION = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_DEFINITION).build();

    public static final ResourceTypeCode SUBSTANCE_NUCLEIC_ACID = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_NUCLEIC_ACID).build();

    public static final ResourceTypeCode SUBSTANCE_POLYMER = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_POLYMER).build();

    public static final ResourceTypeCode SUBSTANCE_PROTEIN = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_PROTEIN).build();

    public static final ResourceTypeCode SUBSTANCE_REFERENCE_INFORMATION = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_REFERENCE_INFORMATION).build();

    public static final ResourceTypeCode SUBSTANCE_SOURCE_MATERIAL = ResourceTypeCode.builder().value(ResourceType.SUBSTANCE_SOURCE_MATERIAL).build();

    public static final ResourceTypeCode SUPPLY_DELIVERY = ResourceTypeCode.builder().value(ResourceType.SUPPLY_DELIVERY).build();

    public static final ResourceTypeCode SUPPLY_REQUEST = ResourceTypeCode.builder().value(ResourceType.SUPPLY_REQUEST).build();

    public static final ResourceTypeCode TASK = ResourceTypeCode.builder().value(ResourceType.TASK).build();

    public static final ResourceTypeCode TERMINOLOGY_CAPABILITIES = ResourceTypeCode.builder().value(ResourceType.TERMINOLOGY_CAPABILITIES).build();

    public static final ResourceTypeCode TEST_PLAN = ResourceTypeCode.builder().value(ResourceType.TEST_PLAN).build();

    public static final ResourceTypeCode TEST_REPORT = ResourceTypeCode.builder().value(ResourceType.TEST_REPORT).build();

    public static final ResourceTypeCode TEST_SCRIPT = ResourceTypeCode.builder().value(ResourceType.TEST_SCRIPT).build();

    public static final ResourceTypeCode TRANSPORT = ResourceTypeCode.builder().value(ResourceType.TRANSPORT).build();

    public static final ResourceTypeCode VALUE_SET = ResourceTypeCode.builder().value(ResourceType.VALUE_SET).build();

    public static final ResourceTypeCode VERIFICATION_RESULT = ResourceTypeCode.builder().value(ResourceType.VERIFICATION_RESULT).build();

    public static final ResourceTypeCode VISION_PRESCRIPTION = ResourceTypeCode.builder().value(ResourceType.VISION_PRESCRIPTION).build();

    private volatile int hashCode;

    private ResourceTypeCode(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ResourceTypeCode as an enum constant.
     */
    public ResourceType getValueAsEnum() {
        return (value != null) ? ResourceType.from(value) : null;
    }

    /**
     * Factory method for creating ResourceTypeCode objects from a passed enum value.
     */
    public static ResourceTypeCode of(ResourceType value) {
        switch (value) {
        case ACCOUNT:
            return ACCOUNT;
        case ACTIVITY_DEFINITION:
            return ACTIVITY_DEFINITION;
        case ACTOR_DEFINITION:
            return ACTOR_DEFINITION;
        case ADMINISTRABLE_PRODUCT_DEFINITION:
            return ADMINISTRABLE_PRODUCT_DEFINITION;
        case ADVERSE_EVENT:
            return ADVERSE_EVENT;
        case ALLERGY_INTOLERANCE:
            return ALLERGY_INTOLERANCE;
        case APPOINTMENT:
            return APPOINTMENT;
        case APPOINTMENT_RESPONSE:
            return APPOINTMENT_RESPONSE;
        case ARTIFACT_ASSESSMENT:
            return ARTIFACT_ASSESSMENT;
        case AUDIT_EVENT:
            return AUDIT_EVENT;
        case BASIC:
            return BASIC;
        case BINARY:
            return BINARY;
        case BIOLOGICALLY_DERIVED_PRODUCT:
            return BIOLOGICALLY_DERIVED_PRODUCT;
        case BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE:
            return BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE;
        case BODY_STRUCTURE:
            return BODY_STRUCTURE;
        case BUNDLE:
            return BUNDLE;
        case CAPABILITY_STATEMENT:
            return CAPABILITY_STATEMENT;
        case CARE_PLAN:
            return CARE_PLAN;
        case CARE_TEAM:
            return CARE_TEAM;
        case CHARGE_ITEM:
            return CHARGE_ITEM;
        case CHARGE_ITEM_DEFINITION:
            return CHARGE_ITEM_DEFINITION;
        case CITATION:
            return CITATION;
        case CLAIM:
            return CLAIM;
        case CLAIM_RESPONSE:
            return CLAIM_RESPONSE;
        case CLINICAL_IMPRESSION:
            return CLINICAL_IMPRESSION;
        case CLINICAL_USE_DEFINITION:
            return CLINICAL_USE_DEFINITION;
        case CODE_SYSTEM:
            return CODE_SYSTEM;
        case COMMUNICATION:
            return COMMUNICATION;
        case COMMUNICATION_REQUEST:
            return COMMUNICATION_REQUEST;
        case COMPARTMENT_DEFINITION:
            return COMPARTMENT_DEFINITION;
        case COMPOSITION:
            return COMPOSITION;
        case CONCEPT_MAP:
            return CONCEPT_MAP;
        case CONDITION:
            return CONDITION;
        case CONDITION_DEFINITION:
            return CONDITION_DEFINITION;
        case CONSENT:
            return CONSENT;
        case CONTRACT:
            return CONTRACT;
        case COVERAGE:
            return COVERAGE;
        case COVERAGE_ELIGIBILITY_REQUEST:
            return COVERAGE_ELIGIBILITY_REQUEST;
        case COVERAGE_ELIGIBILITY_RESPONSE:
            return COVERAGE_ELIGIBILITY_RESPONSE;
        case DETECTED_ISSUE:
            return DETECTED_ISSUE;
        case DEVICE:
            return DEVICE;
        case DEVICE_ASSOCIATION:
            return DEVICE_ASSOCIATION;
        case DEVICE_DEFINITION:
            return DEVICE_DEFINITION;
        case DEVICE_DISPENSE:
            return DEVICE_DISPENSE;
        case DEVICE_METRIC:
            return DEVICE_METRIC;
        case DEVICE_REQUEST:
            return DEVICE_REQUEST;
        case DEVICE_USAGE:
            return DEVICE_USAGE;
        case DIAGNOSTIC_REPORT:
            return DIAGNOSTIC_REPORT;
        case DOCUMENT_REFERENCE:
            return DOCUMENT_REFERENCE;
        case ENCOUNTER:
            return ENCOUNTER;
        case ENCOUNTER_HISTORY:
            return ENCOUNTER_HISTORY;
        case ENDPOINT:
            return ENDPOINT;
        case ENROLLMENT_REQUEST:
            return ENROLLMENT_REQUEST;
        case ENROLLMENT_RESPONSE:
            return ENROLLMENT_RESPONSE;
        case EPISODE_OF_CARE:
            return EPISODE_OF_CARE;
        case EVENT_DEFINITION:
            return EVENT_DEFINITION;
        case EVIDENCE:
            return EVIDENCE;
        case EVIDENCE_REPORT:
            return EVIDENCE_REPORT;
        case EVIDENCE_VARIABLE:
            return EVIDENCE_VARIABLE;
        case EXAMPLE_SCENARIO:
            return EXAMPLE_SCENARIO;
        case EXPLANATION_OF_BENEFIT:
            return EXPLANATION_OF_BENEFIT;
        case FAMILY_MEMBER_HISTORY:
            return FAMILY_MEMBER_HISTORY;
        case FLAG:
            return FLAG;
        case FORMULARY_ITEM:
            return FORMULARY_ITEM;
        case GENOMIC_STUDY:
            return GENOMIC_STUDY;
        case GOAL:
            return GOAL;
        case GRAPH_DEFINITION:
            return GRAPH_DEFINITION;
        case GROUP:
            return GROUP;
        case GUIDANCE_RESPONSE:
            return GUIDANCE_RESPONSE;
        case HEALTHCARE_SERVICE:
            return HEALTHCARE_SERVICE;
        case IMAGING_SELECTION:
            return IMAGING_SELECTION;
        case IMAGING_STUDY:
            return IMAGING_STUDY;
        case IMMUNIZATION:
            return IMMUNIZATION;
        case IMMUNIZATION_EVALUATION:
            return IMMUNIZATION_EVALUATION;
        case IMMUNIZATION_RECOMMENDATION:
            return IMMUNIZATION_RECOMMENDATION;
        case IMPLEMENTATION_GUIDE:
            return IMPLEMENTATION_GUIDE;
        case INGREDIENT:
            return INGREDIENT;
        case INSURANCE_PLAN:
            return INSURANCE_PLAN;
        case INVENTORY_ITEM:
            return INVENTORY_ITEM;
        case INVENTORY_REPORT:
            return INVENTORY_REPORT;
        case INVOICE:
            return INVOICE;
        case LIBRARY:
            return LIBRARY;
        case LINKAGE:
            return LINKAGE;
        case LIST:
            return LIST;
        case LOCATION:
            return LOCATION;
        case MANUFACTURED_ITEM_DEFINITION:
            return MANUFACTURED_ITEM_DEFINITION;
        case MEASURE:
            return MEASURE;
        case MEASURE_REPORT:
            return MEASURE_REPORT;
        case MEDICATION:
            return MEDICATION;
        case MEDICATION_ADMINISTRATION:
            return MEDICATION_ADMINISTRATION;
        case MEDICATION_DISPENSE:
            return MEDICATION_DISPENSE;
        case MEDICATION_KNOWLEDGE:
            return MEDICATION_KNOWLEDGE;
        case MEDICATION_REQUEST:
            return MEDICATION_REQUEST;
        case MEDICATION_STATEMENT:
            return MEDICATION_STATEMENT;
        case MEDICINAL_PRODUCT_DEFINITION:
            return MEDICINAL_PRODUCT_DEFINITION;
        case MESSAGE_DEFINITION:
            return MESSAGE_DEFINITION;
        case MESSAGE_HEADER:
            return MESSAGE_HEADER;
        case MOLECULAR_SEQUENCE:
            return MOLECULAR_SEQUENCE;
        case NAMING_SYSTEM:
            return NAMING_SYSTEM;
        case NUTRITION_INTAKE:
            return NUTRITION_INTAKE;
        case NUTRITION_ORDER:
            return NUTRITION_ORDER;
        case NUTRITION_PRODUCT:
            return NUTRITION_PRODUCT;
        case OBSERVATION:
            return OBSERVATION;
        case OBSERVATION_DEFINITION:
            return OBSERVATION_DEFINITION;
        case OPERATION_DEFINITION:
            return OPERATION_DEFINITION;
        case OPERATION_OUTCOME:
            return OPERATION_OUTCOME;
        case ORGANIZATION:
            return ORGANIZATION;
        case ORGANIZATION_AFFILIATION:
            return ORGANIZATION_AFFILIATION;
        case PACKAGED_PRODUCT_DEFINITION:
            return PACKAGED_PRODUCT_DEFINITION;
        case PARAMETERS:
            return PARAMETERS;
        case PATIENT:
            return PATIENT;
        case PAYMENT_NOTICE:
            return PAYMENT_NOTICE;
        case PAYMENT_RECONCILIATION:
            return PAYMENT_RECONCILIATION;
        case PERMISSION:
            return PERMISSION;
        case PERSON:
            return PERSON;
        case PLAN_DEFINITION:
            return PLAN_DEFINITION;
        case PRACTITIONER:
            return PRACTITIONER;
        case PRACTITIONER_ROLE:
            return PRACTITIONER_ROLE;
        case PROCEDURE:
            return PROCEDURE;
        case PROVENANCE:
            return PROVENANCE;
        case QUESTIONNAIRE:
            return QUESTIONNAIRE;
        case QUESTIONNAIRE_RESPONSE:
            return QUESTIONNAIRE_RESPONSE;
        case REGULATED_AUTHORIZATION:
            return REGULATED_AUTHORIZATION;
        case RELATED_PERSON:
            return RELATED_PERSON;
        case REQUEST_ORCHESTRATION:
            return REQUEST_ORCHESTRATION;
        case REQUIREMENTS:
            return REQUIREMENTS;
        case RESEARCH_STUDY:
            return RESEARCH_STUDY;
        case RESEARCH_SUBJECT:
            return RESEARCH_SUBJECT;
        case RISK_ASSESSMENT:
            return RISK_ASSESSMENT;
        case SCHEDULE:
            return SCHEDULE;
        case SEARCH_PARAMETER:
            return SEARCH_PARAMETER;
        case SERVICE_REQUEST:
            return SERVICE_REQUEST;
        case SLOT:
            return SLOT;
        case SPECIMEN:
            return SPECIMEN;
        case SPECIMEN_DEFINITION:
            return SPECIMEN_DEFINITION;
        case STRUCTURE_DEFINITION:
            return STRUCTURE_DEFINITION;
        case STRUCTURE_MAP:
            return STRUCTURE_MAP;
        case SUBSCRIPTION:
            return SUBSCRIPTION;
        case SUBSCRIPTION_STATUS:
            return SUBSCRIPTION_STATUS;
        case SUBSCRIPTION_TOPIC:
            return SUBSCRIPTION_TOPIC;
        case SUBSTANCE:
            return SUBSTANCE;
        case SUBSTANCE_DEFINITION:
            return SUBSTANCE_DEFINITION;
        case SUBSTANCE_NUCLEIC_ACID:
            return SUBSTANCE_NUCLEIC_ACID;
        case SUBSTANCE_POLYMER:
            return SUBSTANCE_POLYMER;
        case SUBSTANCE_PROTEIN:
            return SUBSTANCE_PROTEIN;
        case SUBSTANCE_REFERENCE_INFORMATION:
            return SUBSTANCE_REFERENCE_INFORMATION;
        case SUBSTANCE_SOURCE_MATERIAL:
            return SUBSTANCE_SOURCE_MATERIAL;
        case SUPPLY_DELIVERY:
            return SUPPLY_DELIVERY;
        case SUPPLY_REQUEST:
            return SUPPLY_REQUEST;
        case TASK:
            return TASK;
        case TERMINOLOGY_CAPABILITIES:
            return TERMINOLOGY_CAPABILITIES;
        case TEST_PLAN:
            return TEST_PLAN;
        case TEST_REPORT:
            return TEST_REPORT;
        case TEST_SCRIPT:
            return TEST_SCRIPT;
        case TRANSPORT:
            return TRANSPORT;
        case VALUE_SET:
            return VALUE_SET;
        case VERIFICATION_RESULT:
            return VERIFICATION_RESULT;
        case VISION_PRESCRIPTION:
            return VISION_PRESCRIPTION;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ResourceTypeCode objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ResourceTypeCode of(java.lang.String value) {
        return of(ResourceType.from(value));
    }

    /**
     * Inherited factory method for creating ResourceTypeCode objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(ResourceType.from(value));
    }

    /**
     * Inherited factory method for creating ResourceTypeCode objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(ResourceType.from(value));
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
        ResourceTypeCode other = (ResourceTypeCode) obj;
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
            return (value != null) ? (Builder) super.value(ResourceType.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for ResourceTypeCode
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(ResourceType value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ResourceTypeCode build() {
            ResourceTypeCode resourceTypeCode = new ResourceTypeCode(this);
            if (validating) {
                validate(resourceTypeCode);
            }
            return resourceTypeCode;
        }

        protected void validate(ResourceTypeCode resourceTypeCode) {
            super.validate(resourceTypeCode);
        }

        protected Builder from(ResourceTypeCode resourceTypeCode) {
            super.from(resourceTypeCode);
            return this;
        }
    }

}
