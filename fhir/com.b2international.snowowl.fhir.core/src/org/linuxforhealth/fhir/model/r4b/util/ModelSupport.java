/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r4b.util;

import java.lang.String;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
//import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.linuxforhealth.fhir.model.annotation.*;
import org.linuxforhealth.fhir.model.r4b.annotation.Binding;
import org.linuxforhealth.fhir.model.r4b.annotation.Constraint;
import org.linuxforhealth.fhir.model.r4b.constraint.spi.ConstraintProvider;
import org.linuxforhealth.fhir.model.r4b.constraint.spi.ModelConstraintProvider;
import org.linuxforhealth.fhir.model.r4b.resource.Resource;
import org.linuxforhealth.fhir.model.r4b.type.*;

/*
 * Modifications:
 * 
 * - Disable unused instance of JUL logger
 */

public final class ModelSupport {
//    private static final Logger log = Logger.getLogger(ModelSupport.class.getName());

    public static final Class<org.linuxforhealth.fhir.model.r4b.type.Boolean> FHIR_BOOLEAN = org.linuxforhealth.fhir.model.r4b.type.Boolean.class;
    public static final Class<org.linuxforhealth.fhir.model.r4b.type.Integer> FHIR_INTEGER = org.linuxforhealth.fhir.model.r4b.type.Integer.class;
    public static final Class<org.linuxforhealth.fhir.model.r4b.type.String> FHIR_STRING = org.linuxforhealth.fhir.model.r4b.type.String.class;
    public static final Class<org.linuxforhealth.fhir.model.r4b.type.Date> FHIR_DATE = org.linuxforhealth.fhir.model.r4b.type.Date.class;
    public static final Class<org.linuxforhealth.fhir.model.r4b.type.Instant> FHIR_INSTANT = org.linuxforhealth.fhir.model.r4b.type.Instant.class;

    private static final Map<Class<?>, Class<?>> CONCRETE_TYPE_MAP = buildConcreteTypeMap();
    private static final List<Class<?>> MODEL_CLASSES = Arrays.asList(
        org.linuxforhealth.fhir.model.r4b.resource.Account.class,
        org.linuxforhealth.fhir.model.r4b.resource.Account.Coverage.class,
        org.linuxforhealth.fhir.model.r4b.resource.Account.Guarantor.class,
        org.linuxforhealth.fhir.model.r4b.resource.ActivityDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ActivityDefinition.DynamicValue.class,
        org.linuxforhealth.fhir.model.r4b.resource.ActivityDefinition.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdministrableProductDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdministrableProductDefinition.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdministrableProductDefinition.RouteOfAdministration.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdministrableProductDefinition.RouteOfAdministration.TargetSpecies.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdministrableProductDefinition.RouteOfAdministration.TargetSpecies.WithdrawalPeriod.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdverseEvent.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdverseEvent.SuspectEntity.class,
        org.linuxforhealth.fhir.model.r4b.resource.AdverseEvent.SuspectEntity.Causality.class,
        org.linuxforhealth.fhir.model.r4b.resource.AllergyIntolerance.class,
        org.linuxforhealth.fhir.model.r4b.resource.AllergyIntolerance.Reaction.class,
        org.linuxforhealth.fhir.model.r4b.resource.Appointment.class,
        org.linuxforhealth.fhir.model.r4b.resource.Appointment.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.AppointmentResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.Agent.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.Agent.Network.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.Entity.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.Entity.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.AuditEvent.Source.class,
        org.linuxforhealth.fhir.model.r4b.resource.Basic.class,
        org.linuxforhealth.fhir.model.r4b.resource.Binary.class,
        org.linuxforhealth.fhir.model.r4b.resource.BiologicallyDerivedProduct.class,
        org.linuxforhealth.fhir.model.r4b.resource.BiologicallyDerivedProduct.Collection.class,
        org.linuxforhealth.fhir.model.r4b.resource.BiologicallyDerivedProduct.Manipulation.class,
        org.linuxforhealth.fhir.model.r4b.resource.BiologicallyDerivedProduct.Processing.class,
        org.linuxforhealth.fhir.model.r4b.resource.BiologicallyDerivedProduct.Storage.class,
        org.linuxforhealth.fhir.model.r4b.resource.BodyStructure.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.Entry.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.Entry.Request.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.Entry.Response.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.Entry.Search.class,
        org.linuxforhealth.fhir.model.r4b.resource.Bundle.Link.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Document.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Implementation.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Messaging.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Messaging.Endpoint.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Messaging.SupportedMessage.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Interaction.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Resource.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Resource.Interaction.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Resource.Operation.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Resource.SearchParam.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Rest.Security.class,
        org.linuxforhealth.fhir.model.r4b.resource.CapabilityStatement.Software.class,
        org.linuxforhealth.fhir.model.r4b.resource.CarePlan.class,
        org.linuxforhealth.fhir.model.r4b.resource.CarePlan.Activity.class,
        org.linuxforhealth.fhir.model.r4b.resource.CarePlan.Activity.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.CareTeam.class,
        org.linuxforhealth.fhir.model.r4b.resource.CareTeam.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.CatalogEntry.class,
        org.linuxforhealth.fhir.model.r4b.resource.CatalogEntry.RelatedEntry.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItem.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItemDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItemDefinition.Applicability.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItemDefinition.PropertyGroup.class,
        org.linuxforhealth.fhir.model.r4b.resource.ChargeItemDefinition.PropertyGroup.PriceComponent.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Abstract.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Classification.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Classification.WhoClassified.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Contributorship.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Contributorship.Entry.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Contributorship.Entry.AffiliationInfo.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Contributorship.Entry.ContributionInstance.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Contributorship.Summary.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Part.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.PublicationForm.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.PublicationForm.PeriodicRelease.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.PublicationForm.PeriodicRelease.DateOfPublication.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.PublicationForm.PublishedIn.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.RelatesTo.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.StatusDate.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Title.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.Version.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.CitedArtifact.WebLocation.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.Classification.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.RelatesTo.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.Summary.class,
        org.linuxforhealth.fhir.model.r4b.resource.Citation.StatusDate.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Accident.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.CareTeam.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Diagnosis.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Insurance.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Item.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Item.Detail.SubDetail.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Payee.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Procedure.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.Related.class,
        org.linuxforhealth.fhir.model.r4b.resource.Claim.SupportingInfo.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.AddItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.AddItem.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.AddItem.Detail.SubDetail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Error.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Insurance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Item.Adjudication.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Item.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Item.Detail.SubDetail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Payment.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.ProcessNote.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClaimResponse.Total.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalImpression.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalImpression.Finding.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalImpression.Investigation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Contraindication.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Contraindication.OtherTherapy.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Indication.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Interaction.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Interaction.Interactant.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.UndesirableEffect.class,
        org.linuxforhealth.fhir.model.r4b.resource.ClinicalUseDefinition.Warning.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.Concept.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.Concept.Designation.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.Concept.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.Filter.class,
        org.linuxforhealth.fhir.model.r4b.resource.CodeSystem.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.Communication.class,
        org.linuxforhealth.fhir.model.r4b.resource.Communication.Payload.class,
        org.linuxforhealth.fhir.model.r4b.resource.CommunicationRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.CommunicationRequest.Payload.class,
        org.linuxforhealth.fhir.model.r4b.resource.CompartmentDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.CompartmentDefinition.Resource.class,
        org.linuxforhealth.fhir.model.r4b.resource.Composition.class,
        org.linuxforhealth.fhir.model.r4b.resource.Composition.Attester.class,
        org.linuxforhealth.fhir.model.r4b.resource.Composition.Event.class,
        org.linuxforhealth.fhir.model.r4b.resource.Composition.RelatesTo.class,
        org.linuxforhealth.fhir.model.r4b.resource.Composition.Section.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.Group.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.Group.Element.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.Group.Element.Target.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.Group.Element.Target.DependsOn.class,
        org.linuxforhealth.fhir.model.r4b.resource.ConceptMap.Group.Unmapped.class,
        org.linuxforhealth.fhir.model.r4b.resource.Condition.class,
        org.linuxforhealth.fhir.model.r4b.resource.Condition.Evidence.class,
        org.linuxforhealth.fhir.model.r4b.resource.Condition.Stage.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.Policy.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.Provision.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.Provision.Actor.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.Provision.Data.class,
        org.linuxforhealth.fhir.model.r4b.resource.Consent.Verification.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.ContentDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Friendly.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Legal.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Rule.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Signer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Action.Subject.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Asset.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Asset.Context.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Asset.ValuedItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Offer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Offer.Answer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.Offer.Party.class,
        org.linuxforhealth.fhir.model.r4b.resource.Contract.Term.SecurityLabel.class,
        org.linuxforhealth.fhir.model.r4b.resource.Coverage.class,
        org.linuxforhealth.fhir.model.r4b.resource.Coverage.Class.class,
        org.linuxforhealth.fhir.model.r4b.resource.Coverage.CostToBeneficiary.class,
        org.linuxforhealth.fhir.model.r4b.resource.Coverage.CostToBeneficiary.Exception.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityRequest.Insurance.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityRequest.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityRequest.Item.Diagnosis.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityRequest.SupportingInfo.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityResponse.Error.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityResponse.Insurance.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityResponse.Insurance.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.CoverageEligibilityResponse.Insurance.Item.Benefit.class,
        org.linuxforhealth.fhir.model.r4b.resource.DetectedIssue.class,
        org.linuxforhealth.fhir.model.r4b.resource.DetectedIssue.Evidence.class,
        org.linuxforhealth.fhir.model.r4b.resource.DetectedIssue.Mitigation.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.DeviceName.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.Specialization.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.UdiCarrier.class,
        org.linuxforhealth.fhir.model.r4b.resource.Device.Version.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.Capability.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.DeviceName.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.Material.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.Specialization.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceDefinition.UdiDeviceIdentifier.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceMetric.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceMetric.Calibration.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceRequest.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.DeviceUseStatement.class,
        org.linuxforhealth.fhir.model.r4b.resource.DiagnosticReport.class,
        org.linuxforhealth.fhir.model.r4b.resource.DiagnosticReport.Media.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentManifest.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentManifest.Related.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentReference.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentReference.Content.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentReference.Context.class,
        org.linuxforhealth.fhir.model.r4b.resource.DocumentReference.RelatesTo.class,
        org.linuxforhealth.fhir.model.r4b.resource.DomainResource.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.ClassHistory.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.Diagnosis.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.Hospitalization.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.Location.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.Encounter.StatusHistory.class,
        org.linuxforhealth.fhir.model.r4b.resource.Endpoint.class,
        org.linuxforhealth.fhir.model.r4b.resource.EnrollmentRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.EnrollmentResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.EpisodeOfCare.class,
        org.linuxforhealth.fhir.model.r4b.resource.EpisodeOfCare.Diagnosis.class,
        org.linuxforhealth.fhir.model.r4b.resource.EpisodeOfCare.StatusHistory.class,
        org.linuxforhealth.fhir.model.r4b.resource.EventDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Certainty.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Statistic.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Statistic.AttributeEstimate.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Statistic.ModelCharacteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Statistic.ModelCharacteristic.Variable.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.Statistic.SampleSize.class,
        org.linuxforhealth.fhir.model.r4b.resource.Evidence.VariableDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceReport.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceReport.RelatesTo.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceReport.Section.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceReport.Subject.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceReport.Subject.Characteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceVariable.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceVariable.Category.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceVariable.Characteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.EvidenceVariable.Characteristic.TimeFromStart.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Actor.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Instance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Instance.ContainedInstance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Instance.Version.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Process.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Process.Step.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Process.Step.Alternative.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExampleScenario.Process.Step.Operation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Accident.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.AddItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.AddItem.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.AddItem.Detail.SubDetail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.BenefitBalance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.BenefitBalance.Financial.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.CareTeam.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Diagnosis.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Insurance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Item.Adjudication.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Item.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Item.Detail.SubDetail.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Payee.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Payment.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Procedure.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.ProcessNote.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Related.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.SupportingInfo.class,
        org.linuxforhealth.fhir.model.r4b.resource.ExplanationOfBenefit.Total.class,
        org.linuxforhealth.fhir.model.r4b.resource.FamilyMemberHistory.class,
        org.linuxforhealth.fhir.model.r4b.resource.FamilyMemberHistory.Condition.class,
        org.linuxforhealth.fhir.model.r4b.resource.Flag.class,
        org.linuxforhealth.fhir.model.r4b.resource.Goal.class,
        org.linuxforhealth.fhir.model.r4b.resource.Goal.Target.class,
        org.linuxforhealth.fhir.model.r4b.resource.GraphDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.GraphDefinition.Link.class,
        org.linuxforhealth.fhir.model.r4b.resource.GraphDefinition.Link.Target.class,
        org.linuxforhealth.fhir.model.r4b.resource.GraphDefinition.Link.Target.Compartment.class,
        org.linuxforhealth.fhir.model.r4b.resource.Group.class,
        org.linuxforhealth.fhir.model.r4b.resource.Group.Characteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.Group.Member.class,
        org.linuxforhealth.fhir.model.r4b.resource.GuidanceResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.HealthcareService.class,
        org.linuxforhealth.fhir.model.r4b.resource.HealthcareService.AvailableTime.class,
        org.linuxforhealth.fhir.model.r4b.resource.HealthcareService.Eligibility.class,
        org.linuxforhealth.fhir.model.r4b.resource.HealthcareService.NotAvailable.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImagingStudy.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImagingStudy.Series.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImagingStudy.Series.Instance.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImagingStudy.Series.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Immunization.class,
        org.linuxforhealth.fhir.model.r4b.resource.Immunization.Education.class,
        org.linuxforhealth.fhir.model.r4b.resource.Immunization.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Immunization.ProtocolApplied.class,
        org.linuxforhealth.fhir.model.r4b.resource.Immunization.Reaction.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImmunizationEvaluation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImmunizationRecommendation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImmunizationRecommendation.Recommendation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImmunizationRecommendation.Recommendation.DateCriterion.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.Grouping.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.Page.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.Resource.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Definition.Template.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.DependsOn.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Global.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Manifest.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Manifest.Page.class,
        org.linuxforhealth.fhir.model.r4b.resource.ImplementationGuide.Manifest.Resource.class,
        org.linuxforhealth.fhir.model.r4b.resource.Ingredient.class,
        org.linuxforhealth.fhir.model.r4b.resource.Ingredient.Manufacturer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Ingredient.Substance.class,
        org.linuxforhealth.fhir.model.r4b.resource.Ingredient.Substance.Strength.class,
        org.linuxforhealth.fhir.model.r4b.resource.Ingredient.Substance.Strength.ReferenceStrength.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Contact.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Coverage.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Coverage.Benefit.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Coverage.Benefit.Limit.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Plan.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Plan.GeneralCost.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Plan.SpecificCost.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Plan.SpecificCost.Benefit.class,
        org.linuxforhealth.fhir.model.r4b.resource.InsurancePlan.Plan.SpecificCost.Benefit.Cost.class,
        org.linuxforhealth.fhir.model.r4b.resource.Invoice.class,
        org.linuxforhealth.fhir.model.r4b.resource.Invoice.LineItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.Invoice.LineItem.PriceComponent.class,
        org.linuxforhealth.fhir.model.r4b.resource.Invoice.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.Library.class,
        org.linuxforhealth.fhir.model.r4b.resource.Linkage.class,
        org.linuxforhealth.fhir.model.r4b.resource.Linkage.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.List.class,
        org.linuxforhealth.fhir.model.r4b.resource.List.Entry.class,
        org.linuxforhealth.fhir.model.r4b.resource.Location.class,
        org.linuxforhealth.fhir.model.r4b.resource.Location.HoursOfOperation.class,
        org.linuxforhealth.fhir.model.r4b.resource.Location.Position.class,
        org.linuxforhealth.fhir.model.r4b.resource.ManufacturedItemDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ManufacturedItemDefinition.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.Group.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.Group.Population.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.Group.Stratifier.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.Group.Stratifier.Component.class,
        org.linuxforhealth.fhir.model.r4b.resource.Measure.SupplementalData.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.Population.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.Stratifier.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.Stratifier.Stratum.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.Stratifier.Stratum.Component.class,
        org.linuxforhealth.fhir.model.r4b.resource.MeasureReport.Group.Stratifier.Stratum.Population.class,
        org.linuxforhealth.fhir.model.r4b.resource.Media.class,
        org.linuxforhealth.fhir.model.r4b.resource.Medication.class,
        org.linuxforhealth.fhir.model.r4b.resource.Medication.Batch.class,
        org.linuxforhealth.fhir.model.r4b.resource.Medication.Ingredient.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationAdministration.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationAdministration.Dosage.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationAdministration.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationDispense.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationDispense.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationDispense.Substitution.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.AdministrationGuidelines.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.AdministrationGuidelines.Dosage.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Cost.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.DrugCharacteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Ingredient.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Kinetics.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.MedicineClassification.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.MonitoringProgram.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Monograph.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Packaging.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Regulatory.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Regulatory.MaxDispense.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Regulatory.Schedule.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.Regulatory.Substitution.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationKnowledge.RelatedMedicationKnowledge.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationRequest.DispenseRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationRequest.DispenseRequest.InitialFill.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationRequest.Substitution.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicationStatement.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Characteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Contact.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.CrossReference.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Name.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Name.CountryLanguage.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Name.NamePart.class,
        org.linuxforhealth.fhir.model.r4b.resource.MedicinalProductDefinition.Operation.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageDefinition.AllowedResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageDefinition.Focus.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageHeader.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageHeader.Destination.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageHeader.Response.class,
        org.linuxforhealth.fhir.model.r4b.resource.MessageHeader.Source.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.Quality.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.Quality.Roc.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.ReferenceSeq.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.Repository.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.StructureVariant.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.StructureVariant.Inner.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.StructureVariant.Outer.class,
        org.linuxforhealth.fhir.model.r4b.resource.MolecularSequence.Variant.class,
        org.linuxforhealth.fhir.model.r4b.resource.NamingSystem.class,
        org.linuxforhealth.fhir.model.r4b.resource.NamingSystem.UniqueId.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.EnteralFormula.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.EnteralFormula.Administration.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.OralDiet.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.OralDiet.Nutrient.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.OralDiet.Texture.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionOrder.Supplement.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionProduct.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionProduct.Ingredient.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionProduct.Instance.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionProduct.Nutrient.class,
        org.linuxforhealth.fhir.model.r4b.resource.NutritionProduct.ProductCharacteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.Observation.class,
        org.linuxforhealth.fhir.model.r4b.resource.Observation.Component.class,
        org.linuxforhealth.fhir.model.r4b.resource.Observation.ReferenceRange.class,
        org.linuxforhealth.fhir.model.r4b.resource.ObservationDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ObservationDefinition.QualifiedInterval.class,
        org.linuxforhealth.fhir.model.r4b.resource.ObservationDefinition.QuantitativeDetails.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationDefinition.Overload.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationDefinition.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationDefinition.Parameter.Binding.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationDefinition.Parameter.ReferencedFrom.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationOutcome.class,
        org.linuxforhealth.fhir.model.r4b.resource.OperationOutcome.Issue.class,
        org.linuxforhealth.fhir.model.r4b.resource.Organization.class,
        org.linuxforhealth.fhir.model.r4b.resource.Organization.Contact.class,
        org.linuxforhealth.fhir.model.r4b.resource.OrganizationAffiliation.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.LegalStatusOfSupply.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.Package.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.Package.ContainedItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.Package.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.PackagedProductDefinition.Package.ShelfLifeStorage.class,
        org.linuxforhealth.fhir.model.r4b.resource.Parameters.class,
        org.linuxforhealth.fhir.model.r4b.resource.Parameters.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.Patient.class,
        org.linuxforhealth.fhir.model.r4b.resource.Patient.Communication.class,
        org.linuxforhealth.fhir.model.r4b.resource.Patient.Contact.class,
        org.linuxforhealth.fhir.model.r4b.resource.Patient.Link.class,
        org.linuxforhealth.fhir.model.r4b.resource.PaymentNotice.class,
        org.linuxforhealth.fhir.model.r4b.resource.PaymentReconciliation.class,
        org.linuxforhealth.fhir.model.r4b.resource.PaymentReconciliation.Detail.class,
        org.linuxforhealth.fhir.model.r4b.resource.PaymentReconciliation.ProcessNote.class,
        org.linuxforhealth.fhir.model.r4b.resource.Person.class,
        org.linuxforhealth.fhir.model.r4b.resource.Person.Link.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Action.Condition.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Action.DynamicValue.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Action.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Action.RelatedAction.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Goal.class,
        org.linuxforhealth.fhir.model.r4b.resource.PlanDefinition.Goal.Target.class,
        org.linuxforhealth.fhir.model.r4b.resource.Practitioner.class,
        org.linuxforhealth.fhir.model.r4b.resource.Practitioner.Qualification.class,
        org.linuxforhealth.fhir.model.r4b.resource.PractitionerRole.class,
        org.linuxforhealth.fhir.model.r4b.resource.PractitionerRole.AvailableTime.class,
        org.linuxforhealth.fhir.model.r4b.resource.PractitionerRole.NotAvailable.class,
        org.linuxforhealth.fhir.model.r4b.resource.Procedure.class,
        org.linuxforhealth.fhir.model.r4b.resource.Procedure.FocalDevice.class,
        org.linuxforhealth.fhir.model.r4b.resource.Procedure.Performer.class,
        org.linuxforhealth.fhir.model.r4b.resource.Provenance.class,
        org.linuxforhealth.fhir.model.r4b.resource.Provenance.Agent.class,
        org.linuxforhealth.fhir.model.r4b.resource.Provenance.Entity.class,
        org.linuxforhealth.fhir.model.r4b.resource.Questionnaire.class,
        org.linuxforhealth.fhir.model.r4b.resource.Questionnaire.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.Questionnaire.Item.AnswerOption.class,
        org.linuxforhealth.fhir.model.r4b.resource.Questionnaire.Item.EnableWhen.class,
        org.linuxforhealth.fhir.model.r4b.resource.Questionnaire.Item.Initial.class,
        org.linuxforhealth.fhir.model.r4b.resource.QuestionnaireResponse.class,
        org.linuxforhealth.fhir.model.r4b.resource.QuestionnaireResponse.Item.class,
        org.linuxforhealth.fhir.model.r4b.resource.QuestionnaireResponse.Item.Answer.class,
        org.linuxforhealth.fhir.model.r4b.resource.RegulatedAuthorization.class,
        org.linuxforhealth.fhir.model.r4b.resource.RegulatedAuthorization.Case.class,
        org.linuxforhealth.fhir.model.r4b.resource.RelatedPerson.class,
        org.linuxforhealth.fhir.model.r4b.resource.RelatedPerson.Communication.class,
        org.linuxforhealth.fhir.model.r4b.resource.RequestGroup.class,
        org.linuxforhealth.fhir.model.r4b.resource.RequestGroup.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.RequestGroup.Action.Condition.class,
        org.linuxforhealth.fhir.model.r4b.resource.RequestGroup.Action.RelatedAction.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchElementDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchElementDefinition.Characteristic.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchStudy.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchStudy.Arm.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchStudy.Objective.class,
        org.linuxforhealth.fhir.model.r4b.resource.ResearchSubject.class,
        org.linuxforhealth.fhir.model.r4b.resource.Resource.class,
        org.linuxforhealth.fhir.model.r4b.resource.RiskAssessment.class,
        org.linuxforhealth.fhir.model.r4b.resource.RiskAssessment.Prediction.class,
        org.linuxforhealth.fhir.model.r4b.resource.Schedule.class,
        org.linuxforhealth.fhir.model.r4b.resource.SearchParameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.SearchParameter.Component.class,
        org.linuxforhealth.fhir.model.r4b.resource.ServiceRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.Slot.class,
        org.linuxforhealth.fhir.model.r4b.resource.Specimen.class,
        org.linuxforhealth.fhir.model.r4b.resource.Specimen.Collection.class,
        org.linuxforhealth.fhir.model.r4b.resource.Specimen.Container.class,
        org.linuxforhealth.fhir.model.r4b.resource.Specimen.Processing.class,
        org.linuxforhealth.fhir.model.r4b.resource.SpecimenDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.SpecimenDefinition.TypeTested.class,
        org.linuxforhealth.fhir.model.r4b.resource.SpecimenDefinition.TypeTested.Container.class,
        org.linuxforhealth.fhir.model.r4b.resource.SpecimenDefinition.TypeTested.Container.Additive.class,
        org.linuxforhealth.fhir.model.r4b.resource.SpecimenDefinition.TypeTested.Handling.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureDefinition.Context.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureDefinition.Differential.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureDefinition.Mapping.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureDefinition.Snapshot.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Input.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Rule.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Rule.Dependent.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Rule.Source.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Rule.Target.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Group.Rule.Target.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.StructureMap.Structure.class,
        org.linuxforhealth.fhir.model.r4b.resource.Subscription.class,
        org.linuxforhealth.fhir.model.r4b.resource.Subscription.Channel.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionStatus.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionStatus.NotificationEvent.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.CanFilterBy.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.EventTrigger.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.NotificationShape.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.ResourceTrigger.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubscriptionTopic.ResourceTrigger.QueryCriteria.class,
        org.linuxforhealth.fhir.model.r4b.resource.Substance.class,
        org.linuxforhealth.fhir.model.r4b.resource.Substance.Ingredient.class,
        org.linuxforhealth.fhir.model.r4b.resource.Substance.Instance.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Code.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Moiety.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.MolecularWeight.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Name.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Name.Official.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Property.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Relationship.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.SourceMaterial.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Structure.class,
        org.linuxforhealth.fhir.model.r4b.resource.SubstanceDefinition.Structure.Representation.class,
        org.linuxforhealth.fhir.model.r4b.resource.SupplyDelivery.class,
        org.linuxforhealth.fhir.model.r4b.resource.SupplyDelivery.SuppliedItem.class,
        org.linuxforhealth.fhir.model.r4b.resource.SupplyRequest.class,
        org.linuxforhealth.fhir.model.r4b.resource.SupplyRequest.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.Task.class,
        org.linuxforhealth.fhir.model.r4b.resource.Task.Input.class,
        org.linuxforhealth.fhir.model.r4b.resource.Task.Output.class,
        org.linuxforhealth.fhir.model.r4b.resource.Task.Restriction.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Closure.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.CodeSystem.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.CodeSystem.Version.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.CodeSystem.Version.Filter.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Expansion.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Expansion.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Implementation.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Software.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.Translation.class,
        org.linuxforhealth.fhir.model.r4b.resource.TerminologyCapabilities.ValidateCode.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Participant.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Setup.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Setup.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Setup.Action.Assert.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Setup.Action.Operation.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Teardown.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Teardown.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Test.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestReport.Test.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Destination.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Fixture.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Metadata.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Metadata.Capability.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Metadata.Link.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Origin.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Setup.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Setup.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Setup.Action.Assert.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Setup.Action.Operation.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Setup.Action.Operation.RequestHeader.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Teardown.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Teardown.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Test.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Test.Action.class,
        org.linuxforhealth.fhir.model.r4b.resource.TestScript.Variable.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Compose.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Compose.Include.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Compose.Include.Concept.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Compose.Include.Concept.Designation.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Compose.Include.Filter.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Expansion.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Expansion.Contains.class,
        org.linuxforhealth.fhir.model.r4b.resource.ValueSet.Expansion.Parameter.class,
        org.linuxforhealth.fhir.model.r4b.resource.VerificationResult.class,
        org.linuxforhealth.fhir.model.r4b.resource.VerificationResult.Attestation.class,
        org.linuxforhealth.fhir.model.r4b.resource.VerificationResult.PrimarySource.class,
        org.linuxforhealth.fhir.model.r4b.resource.VerificationResult.Validator.class,
        org.linuxforhealth.fhir.model.r4b.resource.VisionPrescription.class,
        org.linuxforhealth.fhir.model.r4b.resource.VisionPrescription.LensSpecification.class,
        org.linuxforhealth.fhir.model.r4b.resource.VisionPrescription.LensSpecification.Prism.class,
        org.linuxforhealth.fhir.model.r4b.type.Address.class,
        org.linuxforhealth.fhir.model.r4b.type.Age.class,
        org.linuxforhealth.fhir.model.r4b.type.Annotation.class,
        org.linuxforhealth.fhir.model.r4b.type.Attachment.class,
        org.linuxforhealth.fhir.model.r4b.type.BackboneElement.class,
        org.linuxforhealth.fhir.model.r4b.type.Base64Binary.class,
        org.linuxforhealth.fhir.model.r4b.type.Boolean.class,
        org.linuxforhealth.fhir.model.r4b.type.Canonical.class,
        org.linuxforhealth.fhir.model.r4b.type.Code.class,
        org.linuxforhealth.fhir.model.r4b.type.CodeableConcept.class,
        org.linuxforhealth.fhir.model.r4b.type.CodeableReference.class,
        org.linuxforhealth.fhir.model.r4b.type.Coding.class,
        org.linuxforhealth.fhir.model.r4b.type.ContactDetail.class,
        org.linuxforhealth.fhir.model.r4b.type.ContactPoint.class,
        org.linuxforhealth.fhir.model.r4b.type.Contributor.class,
        org.linuxforhealth.fhir.model.r4b.type.Count.class,
        org.linuxforhealth.fhir.model.r4b.type.DataRequirement.class,
        org.linuxforhealth.fhir.model.r4b.type.DataRequirement.CodeFilter.class,
        org.linuxforhealth.fhir.model.r4b.type.DataRequirement.DateFilter.class,
        org.linuxforhealth.fhir.model.r4b.type.DataRequirement.Sort.class,
        org.linuxforhealth.fhir.model.r4b.type.Date.class,
        org.linuxforhealth.fhir.model.r4b.type.DateTime.class,
        org.linuxforhealth.fhir.model.r4b.type.Decimal.class,
        org.linuxforhealth.fhir.model.r4b.type.Distance.class,
        org.linuxforhealth.fhir.model.r4b.type.Dosage.class,
        org.linuxforhealth.fhir.model.r4b.type.Dosage.DoseAndRate.class,
        org.linuxforhealth.fhir.model.r4b.type.Duration.class,
        org.linuxforhealth.fhir.model.r4b.type.Element.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Base.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Binding.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Constraint.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Example.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Mapping.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Slicing.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Slicing.Discriminator.class,
        org.linuxforhealth.fhir.model.r4b.type.ElementDefinition.Type.class,
        org.linuxforhealth.fhir.model.r4b.type.Expression.class,
        org.linuxforhealth.fhir.model.r4b.type.Extension.class,
        org.linuxforhealth.fhir.model.r4b.type.HumanName.class,
        org.linuxforhealth.fhir.model.r4b.type.Id.class,
        org.linuxforhealth.fhir.model.r4b.type.Identifier.class,
        org.linuxforhealth.fhir.model.r4b.type.Instant.class,
        org.linuxforhealth.fhir.model.r4b.type.Integer.class,
        org.linuxforhealth.fhir.model.r4b.type.Markdown.class,
        org.linuxforhealth.fhir.model.r4b.type.MarketingStatus.class,
        org.linuxforhealth.fhir.model.r4b.type.Meta.class,
        org.linuxforhealth.fhir.model.r4b.type.Money.class,
        org.linuxforhealth.fhir.model.r4b.type.MoneyQuantity.class,
        org.linuxforhealth.fhir.model.r4b.type.Narrative.class,
        org.linuxforhealth.fhir.model.r4b.type.Oid.class,
        org.linuxforhealth.fhir.model.r4b.type.ParameterDefinition.class,
        org.linuxforhealth.fhir.model.r4b.type.Period.class,
        org.linuxforhealth.fhir.model.r4b.type.Population.class,
        org.linuxforhealth.fhir.model.r4b.type.PositiveInt.class,
        org.linuxforhealth.fhir.model.r4b.type.ProdCharacteristic.class,
        org.linuxforhealth.fhir.model.r4b.type.ProductShelfLife.class,
        org.linuxforhealth.fhir.model.r4b.type.Quantity.class,
        org.linuxforhealth.fhir.model.r4b.type.Range.class,
        org.linuxforhealth.fhir.model.r4b.type.Ratio.class,
        org.linuxforhealth.fhir.model.r4b.type.RatioRange.class,
        org.linuxforhealth.fhir.model.r4b.type.Reference.class,
        org.linuxforhealth.fhir.model.r4b.type.RelatedArtifact.class,
        org.linuxforhealth.fhir.model.r4b.type.SampledData.class,
        org.linuxforhealth.fhir.model.r4b.type.Signature.class,
        org.linuxforhealth.fhir.model.r4b.type.SimpleQuantity.class,
        org.linuxforhealth.fhir.model.r4b.type.String.class,
        org.linuxforhealth.fhir.model.r4b.type.Time.class,
        org.linuxforhealth.fhir.model.r4b.type.Timing.class,
        org.linuxforhealth.fhir.model.r4b.type.Timing.Repeat.class,
        org.linuxforhealth.fhir.model.r4b.type.TriggerDefinition.class,
        org.linuxforhealth.fhir.model.r4b.type.UnsignedInt.class,
        org.linuxforhealth.fhir.model.r4b.type.Uri.class,
        org.linuxforhealth.fhir.model.r4b.type.Url.class,
        org.linuxforhealth.fhir.model.r4b.type.UsageContext.class,
        org.linuxforhealth.fhir.model.r4b.type.Uuid.class,
        org.linuxforhealth.fhir.model.r4b.type.Xhtml.class
            );
    private static final Map<Class<?>, Map<String, ElementInfo>> MODEL_CLASS_ELEMENT_INFO_MAP = buildModelClassElementInfoMap();
    private static final Map<String, Class<? extends Resource>> RESOURCE_TYPE_MAP = buildResourceTypeMap();
    private static final Set<Class<? extends Resource>> CONCRETE_RESOURCE_TYPES = getResourceTypes().stream()
            .filter(rt -> !isAbstract(rt))
            .collect(Collectors.toSet());
    private static final Map<Class<?>, List<Constraint>> MODEL_CLASS_CONSTRAINT_MAP = buildModelClassConstraintMap();
    // LinkedHashSet is used just to preserve the order, for convenience only
    private static final Set<Class<? extends Element>> CHOICE_ELEMENT_TYPES = new LinkedHashSet<>(Arrays.asList(
            Base64Binary.class,
            org.linuxforhealth.fhir.model.r4b.type.Boolean.class,
            Canonical.class,
            Code.class,
            Date.class,
            DateTime.class,
            Decimal.class,
            Id.class,
            Instant.class,
            org.linuxforhealth.fhir.model.r4b.type.Integer.class,
            Markdown.class,
            Oid.class,
            PositiveInt.class,
            org.linuxforhealth.fhir.model.r4b.type.String.class,
            Time.class,
            UnsignedInt.class,
            Uri.class,
            Url.class,
            Uuid.class,
            Address.class,
            Age.class,
            Annotation.class,
            Attachment.class,
            CodeableConcept.class,
            CodeableReference.class,
            Coding.class,
            ContactPoint.class,
            Count.class,
            Distance.class,
            Duration.class,
            HumanName.class,
            Identifier.class,
            Money.class,
            MoneyQuantity.class, // profiled type
            Period.class,
            Quantity.class,
            Range.class,
            Ratio.class,
            RatioRange.class,
            Reference.class,
            SampledData.class,
            SimpleQuantity.class, // profiled type
            Signature.class,
            Timing.class,
            ContactDetail.class,
            Contributor.class,
            DataRequirement.class,
            Expression.class,
            ParameterDefinition.class,
            RelatedArtifact.class,
            TriggerDefinition.class,
            UsageContext.class,
            Dosage.class,
            Meta.class));
    private static final Set<Class<? extends Element>> DATA_TYPES;
    static {
        // LinkedHashSet is used just to preserve the order, for convenience only
        Set<Class<? extends Element>> dataTypes = new LinkedHashSet<>(CHOICE_ELEMENT_TYPES);
        dataTypes.add(Xhtml.class);
        dataTypes.add(Narrative.class);
        dataTypes.add(Extension.class);
        dataTypes.add(ElementDefinition.class);
        dataTypes.add(MarketingStatus.class);
        dataTypes.add(Population.class);
        dataTypes.add(ProductShelfLife.class);
        dataTypes.add(ProdCharacteristic.class);
        DATA_TYPES = Collections.unmodifiableSet(dataTypes);
    }
    private static final Map<String, Class<?>> DATA_TYPE_MAP = buildDataTypeMap();
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "$index",
        "$this",
        "$total",
        "and",
        "as",
        "contains",
        "day",
        "days",
        "div",
        "false",
        "hour",
        "hours",
        "implies",
        "in",
        "is",
        "millisecond",
        "milliseconds",
        "minute",
        "minutes",
        "mod",
        "month",
        "months",
        "or",
        "seconds",
        "true",
        "week",
        "weeks",
        "xor",
        "year",
        "years",
        "second"
    ));
    private static final Map<String, Class<?>> CODE_SUBTYPE_MAP = buildCodeSubtypeMap();

    private ModelSupport() { }

    /**
     * Calling this method allows us to load/initialize this class during startup.
     */
    public static void init() { }

    public static final class ElementInfo {
        private final String name;
        private final Class<?> type;
        private final Class<?> declaringType;
        private final boolean required;
        private final boolean repeating;
        private final boolean choice;
        private final Set<Class<?>> choiceTypes;
        private final boolean reference;
        private final Set<String> referenceTypes;
        private final Binding binding;
        private final boolean summary;

        private final Set<String> choiceElementNames;

        ElementInfo(String name,
                Class<?> type,
                Class<?> declaringType,
                boolean required,
                boolean repeating,
                boolean choice,
                Set<Class<?>> choiceTypes,
                boolean reference,
                Set<String> referenceTypes,
                Binding binding,
                boolean isSummary) {
            this.name = name;
            this.declaringType = declaringType;
            this.type = type;
            this.required = required;
            this.repeating = repeating;
            this.choice = choice;
            this.choiceTypes = choiceTypes;
            this.reference = reference;
            this.referenceTypes = referenceTypes;
            this.binding = binding;
            this.summary = isSummary;
            Set<String> choiceElementNames = new LinkedHashSet<>();
            if (this.choice) {
                for (Class<?> choiceType : this.choiceTypes) {
                    choiceElementNames.add(getChoiceElementName(this.name, choiceType));
                }
            }
            this.choiceElementNames = Collections.unmodifiableSet(choiceElementNames);
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public Class<?> getDeclaringType() {
            return declaringType;
        }

        public boolean isDeclaredBy(Class<?> type) {
            return declaringType.equals(type);
        }

        public boolean isRequired() {
            return required;
        }

        public boolean isSummary() {
            return summary;
        }

        public boolean isRepeating() {
            return repeating;
        }

        public boolean isChoice() {
            return choice;
        }

        public Set<Class<?>> getChoiceTypes() {
            return choiceTypes;
        }

        public boolean isReference() {
            return reference;
        }

        public Set<String> getReferenceTypes() {
            return referenceTypes;
        }

        public Binding getBinding() {
            return binding;
        }

        public boolean hasBinding() {
            return (binding != null);
        }

        public Set<String> getChoiceElementNames() {
            return choiceElementNames;
        }
    }

    private static Map<String, Class<?>> buildCodeSubtypeMap() {
        Map<String, Class<?>> codeSubtypeMap = new LinkedHashMap<>();
        codeSubtypeMap.put("AccountStatus", org.linuxforhealth.fhir.model.r4b.type.code.AccountStatus.class);
        codeSubtypeMap.put("ActionCardinalityBehavior", org.linuxforhealth.fhir.model.r4b.type.code.ActionCardinalityBehavior.class);
        codeSubtypeMap.put("ActionConditionKind", org.linuxforhealth.fhir.model.r4b.type.code.ActionConditionKind.class);
        codeSubtypeMap.put("ActionGroupingBehavior", org.linuxforhealth.fhir.model.r4b.type.code.ActionGroupingBehavior.class);
        codeSubtypeMap.put("ActionParticipantType", org.linuxforhealth.fhir.model.r4b.type.code.ActionParticipantType.class);
        codeSubtypeMap.put("ActionPrecheckBehavior", org.linuxforhealth.fhir.model.r4b.type.code.ActionPrecheckBehavior.class);
        codeSubtypeMap.put("ActionRelationshipType", org.linuxforhealth.fhir.model.r4b.type.code.ActionRelationshipType.class);
        codeSubtypeMap.put("ActionRequiredBehavior", org.linuxforhealth.fhir.model.r4b.type.code.ActionRequiredBehavior.class);
        codeSubtypeMap.put("ActionSelectionBehavior", org.linuxforhealth.fhir.model.r4b.type.code.ActionSelectionBehavior.class);
        codeSubtypeMap.put("ActivityDefinitionKind", org.linuxforhealth.fhir.model.r4b.type.code.ActivityDefinitionKind.class);
        codeSubtypeMap.put("ActivityParticipantType", org.linuxforhealth.fhir.model.r4b.type.code.ActivityParticipantType.class);
        codeSubtypeMap.put("AddressType", org.linuxforhealth.fhir.model.r4b.type.code.AddressType.class);
        codeSubtypeMap.put("AddressUse", org.linuxforhealth.fhir.model.r4b.type.code.AddressUse.class);
        codeSubtypeMap.put("AdministrativeGender", org.linuxforhealth.fhir.model.r4b.type.code.AdministrativeGender.class);
        codeSubtypeMap.put("AdverseEventActuality", org.linuxforhealth.fhir.model.r4b.type.code.AdverseEventActuality.class);
        codeSubtypeMap.put("AggregationMode", org.linuxforhealth.fhir.model.r4b.type.code.AggregationMode.class);
        codeSubtypeMap.put("AllergyIntoleranceCategory", org.linuxforhealth.fhir.model.r4b.type.code.AllergyIntoleranceCategory.class);
        codeSubtypeMap.put("AllergyIntoleranceCriticality", org.linuxforhealth.fhir.model.r4b.type.code.AllergyIntoleranceCriticality.class);
        codeSubtypeMap.put("AllergyIntoleranceSeverity", org.linuxforhealth.fhir.model.r4b.type.code.AllergyIntoleranceSeverity.class);
        codeSubtypeMap.put("AllergyIntoleranceType", org.linuxforhealth.fhir.model.r4b.type.code.AllergyIntoleranceType.class);
        codeSubtypeMap.put("AppointmentStatus", org.linuxforhealth.fhir.model.r4b.type.code.AppointmentStatus.class);
        codeSubtypeMap.put("AssertionDirectionType", org.linuxforhealth.fhir.model.r4b.type.code.AssertionDirectionType.class);
        codeSubtypeMap.put("AssertionOperatorType", org.linuxforhealth.fhir.model.r4b.type.code.AssertionOperatorType.class);
        codeSubtypeMap.put("AssertionResponseTypes", org.linuxforhealth.fhir.model.r4b.type.code.AssertionResponseTypes.class);
        codeSubtypeMap.put("AuditEventAction", org.linuxforhealth.fhir.model.r4b.type.code.AuditEventAction.class);
        codeSubtypeMap.put("AuditEventAgentNetworkType", org.linuxforhealth.fhir.model.r4b.type.code.AuditEventAgentNetworkType.class);
        codeSubtypeMap.put("AuditEventOutcome", org.linuxforhealth.fhir.model.r4b.type.code.AuditEventOutcome.class);
        codeSubtypeMap.put("BindingStrength", org.linuxforhealth.fhir.model.r4b.type.code.BindingStrength.class);
        codeSubtypeMap.put("BiologicallyDerivedProductCategory", org.linuxforhealth.fhir.model.r4b.type.code.BiologicallyDerivedProductCategory.class);
        codeSubtypeMap.put("BiologicallyDerivedProductStatus", org.linuxforhealth.fhir.model.r4b.type.code.BiologicallyDerivedProductStatus.class);
        codeSubtypeMap.put("BiologicallyDerivedProductStorageScale", org.linuxforhealth.fhir.model.r4b.type.code.BiologicallyDerivedProductStorageScale.class);
        codeSubtypeMap.put("BundleType", org.linuxforhealth.fhir.model.r4b.type.code.BundleType.class);
        codeSubtypeMap.put("CapabilityStatementKind", org.linuxforhealth.fhir.model.r4b.type.code.CapabilityStatementKind.class);
        codeSubtypeMap.put("CarePlanActivityKind", org.linuxforhealth.fhir.model.r4b.type.code.CarePlanActivityKind.class);
        codeSubtypeMap.put("CarePlanActivityStatus", org.linuxforhealth.fhir.model.r4b.type.code.CarePlanActivityStatus.class);
        codeSubtypeMap.put("CarePlanIntent", org.linuxforhealth.fhir.model.r4b.type.code.CarePlanIntent.class);
        codeSubtypeMap.put("CarePlanStatus", org.linuxforhealth.fhir.model.r4b.type.code.CarePlanStatus.class);
        codeSubtypeMap.put("CareTeamStatus", org.linuxforhealth.fhir.model.r4b.type.code.CareTeamStatus.class);
        codeSubtypeMap.put("CatalogEntryRelationType", org.linuxforhealth.fhir.model.r4b.type.code.CatalogEntryRelationType.class);
        codeSubtypeMap.put("CharacteristicCombination", org.linuxforhealth.fhir.model.r4b.type.code.CharacteristicCombination.class);
        codeSubtypeMap.put("ChargeItemDefinitionPriceComponentType", org.linuxforhealth.fhir.model.r4b.type.code.ChargeItemDefinitionPriceComponentType.class);
        codeSubtypeMap.put("ChargeItemStatus", org.linuxforhealth.fhir.model.r4b.type.code.ChargeItemStatus.class);
        codeSubtypeMap.put("ClaimResponseStatus", org.linuxforhealth.fhir.model.r4b.type.code.ClaimResponseStatus.class);
        codeSubtypeMap.put("ClaimStatus", org.linuxforhealth.fhir.model.r4b.type.code.ClaimStatus.class);
        codeSubtypeMap.put("ClinicalImpressionStatus", org.linuxforhealth.fhir.model.r4b.type.code.ClinicalImpressionStatus.class);
        codeSubtypeMap.put("ClinicalUseDefinitionType", org.linuxforhealth.fhir.model.r4b.type.code.ClinicalUseDefinitionType.class);
        codeSubtypeMap.put("CodeSearchSupport", org.linuxforhealth.fhir.model.r4b.type.code.CodeSearchSupport.class);
        codeSubtypeMap.put("CodeSystemContentMode", org.linuxforhealth.fhir.model.r4b.type.code.CodeSystemContentMode.class);
        codeSubtypeMap.put("CodeSystemHierarchyMeaning", org.linuxforhealth.fhir.model.r4b.type.code.CodeSystemHierarchyMeaning.class);
        codeSubtypeMap.put("CommunicationPriority", org.linuxforhealth.fhir.model.r4b.type.code.CommunicationPriority.class);
        codeSubtypeMap.put("CommunicationRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.CommunicationRequestStatus.class);
        codeSubtypeMap.put("CommunicationStatus", org.linuxforhealth.fhir.model.r4b.type.code.CommunicationStatus.class);
        codeSubtypeMap.put("CompartmentCode", org.linuxforhealth.fhir.model.r4b.type.code.CompartmentCode.class);
        codeSubtypeMap.put("CompartmentType", org.linuxforhealth.fhir.model.r4b.type.code.CompartmentType.class);
        codeSubtypeMap.put("CompositionAttestationMode", org.linuxforhealth.fhir.model.r4b.type.code.CompositionAttestationMode.class);
        codeSubtypeMap.put("CompositionStatus", org.linuxforhealth.fhir.model.r4b.type.code.CompositionStatus.class);
        codeSubtypeMap.put("ConceptMapEquivalence", org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapEquivalence.class);
        codeSubtypeMap.put("ConceptMapGroupUnmappedMode", org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapGroupUnmappedMode.class);
        codeSubtypeMap.put("ConceptSubsumptionOutcome", org.linuxforhealth.fhir.model.r4b.type.code.ConceptSubsumptionOutcome.class);
        codeSubtypeMap.put("ConditionalDeleteStatus", org.linuxforhealth.fhir.model.r4b.type.code.ConditionalDeleteStatus.class);
        codeSubtypeMap.put("ConditionalReadStatus", org.linuxforhealth.fhir.model.r4b.type.code.ConditionalReadStatus.class);
        codeSubtypeMap.put("ConsentDataMeaning", org.linuxforhealth.fhir.model.r4b.type.code.ConsentDataMeaning.class);
        codeSubtypeMap.put("ConsentProvisionType", org.linuxforhealth.fhir.model.r4b.type.code.ConsentProvisionType.class);
        codeSubtypeMap.put("ConsentState", org.linuxforhealth.fhir.model.r4b.type.code.ConsentState.class);
        codeSubtypeMap.put("ConstraintSeverity", org.linuxforhealth.fhir.model.r4b.type.code.ConstraintSeverity.class);
        codeSubtypeMap.put("ContactPointSystem", org.linuxforhealth.fhir.model.r4b.type.code.ContactPointSystem.class);
        codeSubtypeMap.put("ContactPointUse", org.linuxforhealth.fhir.model.r4b.type.code.ContactPointUse.class);
        codeSubtypeMap.put("ContractPublicationStatus", org.linuxforhealth.fhir.model.r4b.type.code.ContractPublicationStatus.class);
        codeSubtypeMap.put("ContractStatus", org.linuxforhealth.fhir.model.r4b.type.code.ContractStatus.class);
        codeSubtypeMap.put("ContributorType", org.linuxforhealth.fhir.model.r4b.type.code.ContributorType.class);
        codeSubtypeMap.put("CoverageStatus", org.linuxforhealth.fhir.model.r4b.type.code.CoverageStatus.class);
        codeSubtypeMap.put("CriteriaNotExistsBehavior", org.linuxforhealth.fhir.model.r4b.type.code.CriteriaNotExistsBehavior.class);
        codeSubtypeMap.put("DataAbsentReason", org.linuxforhealth.fhir.model.r4b.type.code.DataAbsentReason.class);
        codeSubtypeMap.put("DayOfWeek", org.linuxforhealth.fhir.model.r4b.type.code.DayOfWeek.class);
        codeSubtypeMap.put("DaysOfWeek", org.linuxforhealth.fhir.model.r4b.type.code.DaysOfWeek.class);
        codeSubtypeMap.put("DetectedIssueSeverity", org.linuxforhealth.fhir.model.r4b.type.code.DetectedIssueSeverity.class);
        codeSubtypeMap.put("DetectedIssueStatus", org.linuxforhealth.fhir.model.r4b.type.code.DetectedIssueStatus.class);
        codeSubtypeMap.put("DeviceMetricCalibrationState", org.linuxforhealth.fhir.model.r4b.type.code.DeviceMetricCalibrationState.class);
        codeSubtypeMap.put("DeviceMetricCalibrationType", org.linuxforhealth.fhir.model.r4b.type.code.DeviceMetricCalibrationType.class);
        codeSubtypeMap.put("DeviceMetricCategory", org.linuxforhealth.fhir.model.r4b.type.code.DeviceMetricCategory.class);
        codeSubtypeMap.put("DeviceMetricColor", org.linuxforhealth.fhir.model.r4b.type.code.DeviceMetricColor.class);
        codeSubtypeMap.put("DeviceMetricOperationalStatus", org.linuxforhealth.fhir.model.r4b.type.code.DeviceMetricOperationalStatus.class);
        codeSubtypeMap.put("DeviceNameType", org.linuxforhealth.fhir.model.r4b.type.code.DeviceNameType.class);
        codeSubtypeMap.put("DeviceRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.DeviceRequestStatus.class);
        codeSubtypeMap.put("DeviceUseStatementStatus", org.linuxforhealth.fhir.model.r4b.type.code.DeviceUseStatementStatus.class);
        codeSubtypeMap.put("DiagnosticReportStatus", org.linuxforhealth.fhir.model.r4b.type.code.DiagnosticReportStatus.class);
        codeSubtypeMap.put("DiscriminatorType", org.linuxforhealth.fhir.model.r4b.type.code.DiscriminatorType.class);
        codeSubtypeMap.put("DocumentConfidentiality", org.linuxforhealth.fhir.model.r4b.type.code.DocumentConfidentiality.class);
        codeSubtypeMap.put("DocumentMode", org.linuxforhealth.fhir.model.r4b.type.code.DocumentMode.class);
        codeSubtypeMap.put("DocumentReferenceStatus", org.linuxforhealth.fhir.model.r4b.type.code.DocumentReferenceStatus.class);
        codeSubtypeMap.put("DocumentRelationshipType", org.linuxforhealth.fhir.model.r4b.type.code.DocumentRelationshipType.class);
        codeSubtypeMap.put("EligibilityRequestPurpose", org.linuxforhealth.fhir.model.r4b.type.code.EligibilityRequestPurpose.class);
        codeSubtypeMap.put("EligibilityRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.EligibilityRequestStatus.class);
        codeSubtypeMap.put("EligibilityResponsePurpose", org.linuxforhealth.fhir.model.r4b.type.code.EligibilityResponsePurpose.class);
        codeSubtypeMap.put("EligibilityResponseStatus", org.linuxforhealth.fhir.model.r4b.type.code.EligibilityResponseStatus.class);
        codeSubtypeMap.put("EnableWhenBehavior", org.linuxforhealth.fhir.model.r4b.type.code.EnableWhenBehavior.class);
        codeSubtypeMap.put("EncounterLocationStatus", org.linuxforhealth.fhir.model.r4b.type.code.EncounterLocationStatus.class);
        codeSubtypeMap.put("EncounterStatus", org.linuxforhealth.fhir.model.r4b.type.code.EncounterStatus.class);
        codeSubtypeMap.put("EndpointStatus", org.linuxforhealth.fhir.model.r4b.type.code.EndpointStatus.class);
        codeSubtypeMap.put("EnrollmentRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.EnrollmentRequestStatus.class);
        codeSubtypeMap.put("EnrollmentResponseStatus", org.linuxforhealth.fhir.model.r4b.type.code.EnrollmentResponseStatus.class);
        codeSubtypeMap.put("EpisodeOfCareStatus", org.linuxforhealth.fhir.model.r4b.type.code.EpisodeOfCareStatus.class);
        codeSubtypeMap.put("EventCapabilityMode", org.linuxforhealth.fhir.model.r4b.type.code.EventCapabilityMode.class);
        codeSubtypeMap.put("EventTiming", org.linuxforhealth.fhir.model.r4b.type.code.EventTiming.class);
        codeSubtypeMap.put("EvidenceVariableHandling", org.linuxforhealth.fhir.model.r4b.type.code.EvidenceVariableHandling.class);
        codeSubtypeMap.put("ExampleScenarioActorType", org.linuxforhealth.fhir.model.r4b.type.code.ExampleScenarioActorType.class);
        codeSubtypeMap.put("ExplanationOfBenefitStatus", org.linuxforhealth.fhir.model.r4b.type.code.ExplanationOfBenefitStatus.class);
        codeSubtypeMap.put("ExtensionContextType", org.linuxforhealth.fhir.model.r4b.type.code.ExtensionContextType.class);
        codeSubtypeMap.put("FamilyHistoryStatus", org.linuxforhealth.fhir.model.r4b.type.code.FamilyHistoryStatus.class);
        codeSubtypeMap.put("FHIRAllTypes", org.linuxforhealth.fhir.model.r4b.type.code.FHIRAllTypes.class);
        codeSubtypeMap.put("FHIRDefinedType", org.linuxforhealth.fhir.model.r4b.type.code.FHIRDefinedType.class);
        codeSubtypeMap.put("FHIRDeviceStatus", org.linuxforhealth.fhir.model.r4b.type.code.FHIRDeviceStatus.class);
        codeSubtypeMap.put("FHIRSubstanceStatus", org.linuxforhealth.fhir.model.r4b.type.code.FHIRSubstanceStatus.class);
        codeSubtypeMap.put("FHIRVersion", org.linuxforhealth.fhir.model.r4b.type.code.FHIRVersion.class);
        codeSubtypeMap.put("FilterOperator", org.linuxforhealth.fhir.model.r4b.type.code.FilterOperator.class);
        codeSubtypeMap.put("FlagStatus", org.linuxforhealth.fhir.model.r4b.type.code.FlagStatus.class);
        codeSubtypeMap.put("GoalLifecycleStatus", org.linuxforhealth.fhir.model.r4b.type.code.GoalLifecycleStatus.class);
        codeSubtypeMap.put("GraphCompartmentRule", org.linuxforhealth.fhir.model.r4b.type.code.GraphCompartmentRule.class);
        codeSubtypeMap.put("GraphCompartmentUse", org.linuxforhealth.fhir.model.r4b.type.code.GraphCompartmentUse.class);
        codeSubtypeMap.put("GroupMeasure", org.linuxforhealth.fhir.model.r4b.type.code.GroupMeasure.class);
        codeSubtypeMap.put("GroupType", org.linuxforhealth.fhir.model.r4b.type.code.GroupType.class);
        codeSubtypeMap.put("GuidanceResponseStatus", org.linuxforhealth.fhir.model.r4b.type.code.GuidanceResponseStatus.class);
        codeSubtypeMap.put("GuidePageGeneration", org.linuxforhealth.fhir.model.r4b.type.code.GuidePageGeneration.class);
        codeSubtypeMap.put("GuideParameterCode", org.linuxforhealth.fhir.model.r4b.type.code.GuideParameterCode.class);
        codeSubtypeMap.put("HTTPVerb", org.linuxforhealth.fhir.model.r4b.type.code.HTTPVerb.class);
        codeSubtypeMap.put("IdentifierUse", org.linuxforhealth.fhir.model.r4b.type.code.IdentifierUse.class);
        codeSubtypeMap.put("IdentityAssuranceLevel", org.linuxforhealth.fhir.model.r4b.type.code.IdentityAssuranceLevel.class);
        codeSubtypeMap.put("ImagingStudyStatus", org.linuxforhealth.fhir.model.r4b.type.code.ImagingStudyStatus.class);
        codeSubtypeMap.put("ImmunizationEvaluationStatus", org.linuxforhealth.fhir.model.r4b.type.code.ImmunizationEvaluationStatus.class);
        codeSubtypeMap.put("ImmunizationStatus", org.linuxforhealth.fhir.model.r4b.type.code.ImmunizationStatus.class);
        codeSubtypeMap.put("InvoicePriceComponentType", org.linuxforhealth.fhir.model.r4b.type.code.InvoicePriceComponentType.class);
        codeSubtypeMap.put("InvoiceStatus", org.linuxforhealth.fhir.model.r4b.type.code.InvoiceStatus.class);
        codeSubtypeMap.put("IssueSeverity", org.linuxforhealth.fhir.model.r4b.type.code.IssueSeverity.class);
        codeSubtypeMap.put("IssueType", org.linuxforhealth.fhir.model.r4b.type.code.IssueType.class);
        codeSubtypeMap.put("LinkageType", org.linuxforhealth.fhir.model.r4b.type.code.LinkageType.class);
        codeSubtypeMap.put("LinkType", org.linuxforhealth.fhir.model.r4b.type.code.LinkType.class);
        codeSubtypeMap.put("ListMode", org.linuxforhealth.fhir.model.r4b.type.code.ListMode.class);
        codeSubtypeMap.put("ListStatus", org.linuxforhealth.fhir.model.r4b.type.code.ListStatus.class);
        codeSubtypeMap.put("LocationMode", org.linuxforhealth.fhir.model.r4b.type.code.LocationMode.class);
        codeSubtypeMap.put("LocationStatus", org.linuxforhealth.fhir.model.r4b.type.code.LocationStatus.class);
        codeSubtypeMap.put("MeasureReportStatus", org.linuxforhealth.fhir.model.r4b.type.code.MeasureReportStatus.class);
        codeSubtypeMap.put("MeasureReportType", org.linuxforhealth.fhir.model.r4b.type.code.MeasureReportType.class);
        codeSubtypeMap.put("MediaStatus", org.linuxforhealth.fhir.model.r4b.type.code.MediaStatus.class);
        codeSubtypeMap.put("MedicationAdministrationStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationAdministrationStatus.class);
        codeSubtypeMap.put("MedicationDispenseStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationDispenseStatus.class);
        codeSubtypeMap.put("MedicationKnowledgeStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationKnowledgeStatus.class);
        codeSubtypeMap.put("MedicationRequestIntent", org.linuxforhealth.fhir.model.r4b.type.code.MedicationRequestIntent.class);
        codeSubtypeMap.put("MedicationRequestPriority", org.linuxforhealth.fhir.model.r4b.type.code.MedicationRequestPriority.class);
        codeSubtypeMap.put("MedicationRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationRequestStatus.class);
        codeSubtypeMap.put("MedicationStatementStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationStatementStatus.class);
        codeSubtypeMap.put("MedicationStatus", org.linuxforhealth.fhir.model.r4b.type.code.MedicationStatus.class);
        codeSubtypeMap.put("MessageHeaderResponseRequest", org.linuxforhealth.fhir.model.r4b.type.code.MessageHeaderResponseRequest.class);
        codeSubtypeMap.put("MessageSignificanceCategory", org.linuxforhealth.fhir.model.r4b.type.code.MessageSignificanceCategory.class);
        codeSubtypeMap.put("MethodCode", org.linuxforhealth.fhir.model.r4b.type.code.MethodCode.class);
        codeSubtypeMap.put("NameUse", org.linuxforhealth.fhir.model.r4b.type.code.NameUse.class);
        codeSubtypeMap.put("NamingSystemIdentifierType", org.linuxforhealth.fhir.model.r4b.type.code.NamingSystemIdentifierType.class);
        codeSubtypeMap.put("NamingSystemType", org.linuxforhealth.fhir.model.r4b.type.code.NamingSystemType.class);
        codeSubtypeMap.put("NarrativeStatus", org.linuxforhealth.fhir.model.r4b.type.code.NarrativeStatus.class);
        codeSubtypeMap.put("NoteType", org.linuxforhealth.fhir.model.r4b.type.code.NoteType.class);
        codeSubtypeMap.put("NutritionOrderIntent", org.linuxforhealth.fhir.model.r4b.type.code.NutritionOrderIntent.class);
        codeSubtypeMap.put("NutritionOrderStatus", org.linuxforhealth.fhir.model.r4b.type.code.NutritionOrderStatus.class);
        codeSubtypeMap.put("NutritionProductStatus", org.linuxforhealth.fhir.model.r4b.type.code.NutritionProductStatus.class);
        codeSubtypeMap.put("ObservationDataType", org.linuxforhealth.fhir.model.r4b.type.code.ObservationDataType.class);
        codeSubtypeMap.put("ObservationRangeCategory", org.linuxforhealth.fhir.model.r4b.type.code.ObservationRangeCategory.class);
        codeSubtypeMap.put("ObservationStatus", org.linuxforhealth.fhir.model.r4b.type.code.ObservationStatus.class);
        codeSubtypeMap.put("OperationKind", org.linuxforhealth.fhir.model.r4b.type.code.OperationKind.class);
        codeSubtypeMap.put("OperationParameterUse", org.linuxforhealth.fhir.model.r4b.type.code.OperationParameterUse.class);
        codeSubtypeMap.put("OrientationType", org.linuxforhealth.fhir.model.r4b.type.code.OrientationType.class);
        codeSubtypeMap.put("ParameterUse", org.linuxforhealth.fhir.model.r4b.type.code.ParameterUse.class);
        codeSubtypeMap.put("ParticipantRequired", org.linuxforhealth.fhir.model.r4b.type.code.ParticipantRequired.class);
        codeSubtypeMap.put("ParticipantStatus", org.linuxforhealth.fhir.model.r4b.type.code.ParticipantStatus.class);
        codeSubtypeMap.put("ParticipationStatus", org.linuxforhealth.fhir.model.r4b.type.code.ParticipationStatus.class);
        codeSubtypeMap.put("PaymentNoticeStatus", org.linuxforhealth.fhir.model.r4b.type.code.PaymentNoticeStatus.class);
        codeSubtypeMap.put("PaymentReconciliationStatus", org.linuxforhealth.fhir.model.r4b.type.code.PaymentReconciliationStatus.class);
        codeSubtypeMap.put("ProcedureStatus", org.linuxforhealth.fhir.model.r4b.type.code.ProcedureStatus.class);
        codeSubtypeMap.put("PropertyRepresentation", org.linuxforhealth.fhir.model.r4b.type.code.PropertyRepresentation.class);
        codeSubtypeMap.put("PropertyType", org.linuxforhealth.fhir.model.r4b.type.code.PropertyType.class);
        codeSubtypeMap.put("ProvenanceEntityRole", org.linuxforhealth.fhir.model.r4b.type.code.ProvenanceEntityRole.class);
        codeSubtypeMap.put("PublicationStatus", org.linuxforhealth.fhir.model.r4b.type.code.PublicationStatus.class);
        codeSubtypeMap.put("QualityType", org.linuxforhealth.fhir.model.r4b.type.code.QualityType.class);
        codeSubtypeMap.put("QuantityComparator", org.linuxforhealth.fhir.model.r4b.type.code.QuantityComparator.class);
        codeSubtypeMap.put("QuestionnaireItemOperator", org.linuxforhealth.fhir.model.r4b.type.code.QuestionnaireItemOperator.class);
        codeSubtypeMap.put("QuestionnaireItemType", org.linuxforhealth.fhir.model.r4b.type.code.QuestionnaireItemType.class);
        codeSubtypeMap.put("QuestionnaireResponseStatus", org.linuxforhealth.fhir.model.r4b.type.code.QuestionnaireResponseStatus.class);
        codeSubtypeMap.put("ReferenceHandlingPolicy", org.linuxforhealth.fhir.model.r4b.type.code.ReferenceHandlingPolicy.class);
        codeSubtypeMap.put("ReferenceVersionRules", org.linuxforhealth.fhir.model.r4b.type.code.ReferenceVersionRules.class);
        codeSubtypeMap.put("ReferredDocumentStatus", org.linuxforhealth.fhir.model.r4b.type.code.ReferredDocumentStatus.class);
        codeSubtypeMap.put("RelatedArtifactType", org.linuxforhealth.fhir.model.r4b.type.code.RelatedArtifactType.class);
        codeSubtypeMap.put("RemittanceOutcome", org.linuxforhealth.fhir.model.r4b.type.code.RemittanceOutcome.class);
        codeSubtypeMap.put("ReportRelationshipType", org.linuxforhealth.fhir.model.r4b.type.code.ReportRelationshipType.class);
        codeSubtypeMap.put("RepositoryType", org.linuxforhealth.fhir.model.r4b.type.code.RepositoryType.class);
        codeSubtypeMap.put("RequestIntent", org.linuxforhealth.fhir.model.r4b.type.code.RequestIntent.class);
        codeSubtypeMap.put("RequestPriority", org.linuxforhealth.fhir.model.r4b.type.code.RequestPriority.class);
        codeSubtypeMap.put("RequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.RequestStatus.class);
        codeSubtypeMap.put("ResearchElementType", org.linuxforhealth.fhir.model.r4b.type.code.ResearchElementType.class);
        codeSubtypeMap.put("ResearchStudyStatus", org.linuxforhealth.fhir.model.r4b.type.code.ResearchStudyStatus.class);
        codeSubtypeMap.put("ResearchSubjectStatus", org.linuxforhealth.fhir.model.r4b.type.code.ResearchSubjectStatus.class);
        codeSubtypeMap.put("ResourceTypeCode", org.linuxforhealth.fhir.model.r4b.type.code.ResourceTypeCode.class);
        codeSubtypeMap.put("ResourceVersionPolicy", org.linuxforhealth.fhir.model.r4b.type.code.ResourceVersionPolicy.class);
        codeSubtypeMap.put("ResponseType", org.linuxforhealth.fhir.model.r4b.type.code.ResponseType.class);
        codeSubtypeMap.put("RestfulCapabilityMode", org.linuxforhealth.fhir.model.r4b.type.code.RestfulCapabilityMode.class);
        codeSubtypeMap.put("RiskAssessmentStatus", org.linuxforhealth.fhir.model.r4b.type.code.RiskAssessmentStatus.class);
        codeSubtypeMap.put("SearchComparator", org.linuxforhealth.fhir.model.r4b.type.code.SearchComparator.class);
        codeSubtypeMap.put("SearchEntryMode", org.linuxforhealth.fhir.model.r4b.type.code.SearchEntryMode.class);
        codeSubtypeMap.put("SearchModifierCode", org.linuxforhealth.fhir.model.r4b.type.code.SearchModifierCode.class);
        codeSubtypeMap.put("SearchParamType", org.linuxforhealth.fhir.model.r4b.type.code.SearchParamType.class);
        codeSubtypeMap.put("SectionMode", org.linuxforhealth.fhir.model.r4b.type.code.SectionMode.class);
        codeSubtypeMap.put("SequenceType", org.linuxforhealth.fhir.model.r4b.type.code.SequenceType.class);
        codeSubtypeMap.put("ServiceRequestIntent", org.linuxforhealth.fhir.model.r4b.type.code.ServiceRequestIntent.class);
        codeSubtypeMap.put("ServiceRequestPriority", org.linuxforhealth.fhir.model.r4b.type.code.ServiceRequestPriority.class);
        codeSubtypeMap.put("ServiceRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.ServiceRequestStatus.class);
        codeSubtypeMap.put("SlicingRules", org.linuxforhealth.fhir.model.r4b.type.code.SlicingRules.class);
        codeSubtypeMap.put("SlotStatus", org.linuxforhealth.fhir.model.r4b.type.code.SlotStatus.class);
        codeSubtypeMap.put("SortDirection", org.linuxforhealth.fhir.model.r4b.type.code.SortDirection.class);
        codeSubtypeMap.put("SPDXLicense", org.linuxforhealth.fhir.model.r4b.type.code.SPDXLicense.class);
        codeSubtypeMap.put("SpecimenContainedPreference", org.linuxforhealth.fhir.model.r4b.type.code.SpecimenContainedPreference.class);
        codeSubtypeMap.put("SpecimenStatus", org.linuxforhealth.fhir.model.r4b.type.code.SpecimenStatus.class);
        codeSubtypeMap.put("StandardsStatus", org.linuxforhealth.fhir.model.r4b.type.code.StandardsStatus.class);
        codeSubtypeMap.put("Status", org.linuxforhealth.fhir.model.r4b.type.code.Status.class);
        codeSubtypeMap.put("StrandType", org.linuxforhealth.fhir.model.r4b.type.code.StrandType.class);
        codeSubtypeMap.put("StructureDefinitionKind", org.linuxforhealth.fhir.model.r4b.type.code.StructureDefinitionKind.class);
        codeSubtypeMap.put("StructureMapContextType", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapContextType.class);
        codeSubtypeMap.put("StructureMapGroupTypeMode", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapGroupTypeMode.class);
        codeSubtypeMap.put("StructureMapInputMode", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapInputMode.class);
        codeSubtypeMap.put("StructureMapModelMode", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapModelMode.class);
        codeSubtypeMap.put("StructureMapSourceListMode", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapSourceListMode.class);
        codeSubtypeMap.put("StructureMapTargetListMode", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapTargetListMode.class);
        codeSubtypeMap.put("StructureMapTransform", org.linuxforhealth.fhir.model.r4b.type.code.StructureMapTransform.class);
        codeSubtypeMap.put("SubscriptionChannelType", org.linuxforhealth.fhir.model.r4b.type.code.SubscriptionChannelType.class);
        codeSubtypeMap.put("SubscriptionNotificationType", org.linuxforhealth.fhir.model.r4b.type.code.SubscriptionNotificationType.class);
        codeSubtypeMap.put("SubscriptionStatusCode", org.linuxforhealth.fhir.model.r4b.type.code.SubscriptionStatusCode.class);
        codeSubtypeMap.put("SubscriptionTopicFilterBySearchModifier", org.linuxforhealth.fhir.model.r4b.type.code.SubscriptionTopicFilterBySearchModifier.class);
        codeSubtypeMap.put("SupplyDeliveryStatus", org.linuxforhealth.fhir.model.r4b.type.code.SupplyDeliveryStatus.class);
        codeSubtypeMap.put("SupplyRequestStatus", org.linuxforhealth.fhir.model.r4b.type.code.SupplyRequestStatus.class);
        codeSubtypeMap.put("SystemRestfulInteraction", org.linuxforhealth.fhir.model.r4b.type.code.SystemRestfulInteraction.class);
        codeSubtypeMap.put("TaskIntent", org.linuxforhealth.fhir.model.r4b.type.code.TaskIntent.class);
        codeSubtypeMap.put("TaskPriority", org.linuxforhealth.fhir.model.r4b.type.code.TaskPriority.class);
        codeSubtypeMap.put("TaskStatus", org.linuxforhealth.fhir.model.r4b.type.code.TaskStatus.class);
        codeSubtypeMap.put("TestReportActionResult", org.linuxforhealth.fhir.model.r4b.type.code.TestReportActionResult.class);
        codeSubtypeMap.put("TestReportParticipantType", org.linuxforhealth.fhir.model.r4b.type.code.TestReportParticipantType.class);
        codeSubtypeMap.put("TestReportResult", org.linuxforhealth.fhir.model.r4b.type.code.TestReportResult.class);
        codeSubtypeMap.put("TestReportStatus", org.linuxforhealth.fhir.model.r4b.type.code.TestReportStatus.class);
        codeSubtypeMap.put("TestScriptRequestMethodCode", org.linuxforhealth.fhir.model.r4b.type.code.TestScriptRequestMethodCode.class);
        codeSubtypeMap.put("TriggerType", org.linuxforhealth.fhir.model.r4b.type.code.TriggerType.class);
        codeSubtypeMap.put("TypeDerivationRule", org.linuxforhealth.fhir.model.r4b.type.code.TypeDerivationRule.class);
        codeSubtypeMap.put("TypeRestfulInteraction", org.linuxforhealth.fhir.model.r4b.type.code.TypeRestfulInteraction.class);
        codeSubtypeMap.put("UDIEntryType", org.linuxforhealth.fhir.model.r4b.type.code.UDIEntryType.class);
        codeSubtypeMap.put("UnitsOfTime", org.linuxforhealth.fhir.model.r4b.type.code.UnitsOfTime.class);
        codeSubtypeMap.put("Use", org.linuxforhealth.fhir.model.r4b.type.code.Use.class);
        codeSubtypeMap.put("VariableType", org.linuxforhealth.fhir.model.r4b.type.code.VariableType.class);
        codeSubtypeMap.put("VisionBase", org.linuxforhealth.fhir.model.r4b.type.code.VisionBase.class);
        codeSubtypeMap.put("VisionEyes", org.linuxforhealth.fhir.model.r4b.type.code.VisionEyes.class);
        codeSubtypeMap.put("VisionStatus", org.linuxforhealth.fhir.model.r4b.type.code.VisionStatus.class);
        codeSubtypeMap.put("XPathUsageType", org.linuxforhealth.fhir.model.r4b.type.code.XPathUsageType.class);
        return Collections.unmodifiableMap(codeSubtypeMap);
    }

    private static Map<String, Class<?>> buildDataTypeMap() {
        Map<String, Class<?>> dataTypeMap = new LinkedHashMap<>();
        for (Class<?> dataType : DATA_TYPES) {
            dataTypeMap.put(getTypeName(dataType), dataType);
        }
        return Collections.unmodifiableMap(dataTypeMap);
    }

    private static Map<Class<?>, Class<?>> buildConcreteTypeMap() {
        Map<Class<?>, Class<?>> concreteTypeMap = new LinkedHashMap<>();
        concreteTypeMap.put(SimpleQuantity.class, Quantity.class);
        concreteTypeMap.put(MoneyQuantity.class, Quantity.class);
        return Collections.unmodifiableMap(concreteTypeMap);
    }

    private static Map<Class<?>, List<Constraint>> buildModelClassConstraintMap() {
        Map<Class<?>, List<Constraint>> modelClassConstraintMap = new LinkedHashMap<>(1024);
        List<ModelConstraintProvider> providers = ConstraintProvider.providers(ModelConstraintProvider.class);
        for (Class<?> modelClass : getModelClasses()) {
            List<Constraint> constraints = new ArrayList<>();
            for (Class<?> clazz : getClosure(modelClass)) {
                for (Constraint constraint : clazz.getDeclaredAnnotationsByType(Constraint.class)) {
                    constraints.add(Constraint.Factory.createConstraint(
                        constraint.id(),
                        constraint.level(),
                        constraint.location(),
                        constraint.description(),
                        constraint.expression(),
                        constraint.source(),
                        constraint.modelChecked(),
                        constraint.generated()));
                }
            }
            for (ModelConstraintProvider provider : providers) {
                if (provider.appliesTo(modelClass)) {
                    for (Predicate<Constraint> removalPredicate : provider.getRemovalPredicates()) {
                        constraints.removeIf(removalPredicate);
                    }
                    constraints.addAll(provider.getConstraints());
                }
            }
            modelClassConstraintMap.put(modelClass, Collections.unmodifiableList(constraints));
        }
        return Collections.unmodifiableMap(modelClassConstraintMap);
    }

    private static Map<Class<?>, Map<String, ElementInfo>> buildModelClassElementInfoMap() {
        Map<Class<?>, Map<String, ElementInfo>> modelClassElementInfoMap = new LinkedHashMap<>(1024);
        for(Class<?> modelClass : MODEL_CLASSES) {
            Map<String, ElementInfo> elementInfoMap = getElementInfoMap(modelClass, modelClassElementInfoMap);
            modelClassElementInfoMap.put(modelClass, Collections.unmodifiableMap(elementInfoMap));
        }
        return Collections.unmodifiableMap(modelClassElementInfoMap);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Class<? extends Resource>> buildResourceTypeMap() {
        Map<String, Class<? extends Resource>> resourceTypeMap = new LinkedHashMap<>(256);
        for (Class<?> modelClass : getModelClasses()) {
            if (isResourceType(modelClass)) {
                resourceTypeMap.put(modelClass.getSimpleName(), (Class<? extends Resource>) modelClass);
            }
        }
        return Collections.unmodifiableMap(resourceTypeMap);
    }

    private static Map<String, ElementInfo> getElementInfoMap(Class<?> modelClass,
            Map<Class<?>, Map<String,ElementInfo>> elementInfoMapCache) {
        Map<String, ElementInfo> elementInfoMap = new LinkedHashMap<>();

        // Loop through this class and its supertypes to collect ElementInfo for all the fields
        for (Class<?> clazz : getClosure(modelClass)) {
            // If we've already created ElementInfo for this class, then use that
            if (elementInfoMapCache.containsKey(clazz)) {
                elementInfoMap.putAll(elementInfoMapCache.get(clazz));
                continue;
            }

            // Else use reflection and model annotations to construct ElementInfo for all fields in this class
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isVolatile(modifiers)) {
                    continue;
                }

                String elementName = getElementName(field);
                Class<?> type = getFieldType(field);
                Class<?> declaringType = field.getDeclaringClass();
                boolean required = isRequired(field);
                boolean summary = isSummary(field);
                boolean repeating = isRepeating(field);
                boolean choice = isChoice(field);
                boolean reference = isReference(field);
                Binding binding = field.getAnnotation(Binding.class);
                Set<Class<?>> choiceTypes = choice ? Collections.unmodifiableSet(getChoiceTypes(field)) : Collections.emptySet();
                Set<String> referenceTypes = reference ? Collections.unmodifiableSet(getReferenceTypes(field)) : Collections.emptySet();
                elementInfoMap.put(elementName, new ElementInfo(
                        elementName,
                        type,
                        declaringType,
                        required,
                        repeating,
                        choice,
                        choiceTypes,
                        reference,
                        referenceTypes,
                        binding,
                        summary
                    )
                );
            }
        }
        return elementInfoMap;
    }

    /**
     * @param name
     *            the name of the choice element without any type suffix
     * @param type
     *            the model class which represents the choice value for the choice element
     * @return the serialized name of the choice element {@code name} with choice type {@code type}
     */
    public static String getChoiceElementName(String name, Class<?> type) {
        return name + getConcreteType(type).getSimpleName();
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the choice element without any type suffix
     * @return the set of model classes for the allowed types of the specified choice element
     */
    public static Set<Class<?>> getChoiceElementTypes(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.getChoiceTypes();
        }
        return Collections.emptySet();
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the reference element
     * @return a set of Strings which represent the the allowed target types for the reference
     */
    public static Set<String> getReferenceTargetTypes(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.getReferenceTypes();
        }
        return Collections.emptySet();
    }

    private static Set<Class<?>> getChoiceTypes(Field field) {
        return new LinkedHashSet<>(Arrays.asList(field.getAnnotation(Choice.class).value()));
    }

    private static Set<String> getReferenceTypes(Field field) {
        return new LinkedHashSet<>(Arrays.asList(field.getAnnotation(ReferenceTarget.class).value()));
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @return A list of superclasses ordered from parent to child, including the modelClass itself
     */
    public static List<Class<?>> getClosure(Class<?> modelClass) {
        List<Class<?>> closure = new ArrayList<>();
        while (!Object.class.equals(modelClass)) {
            closure.add(modelClass);
            modelClass = modelClass.getSuperclass();
        }
        Collections.reverse(closure);
        return closure;
    }

    /**
     * @param type
     * @return the class for the concrete type of the passed type if it is a profiled type; otherwise the passed type
     *         itself
     */
    public static Class<?> getConcreteType(Class<?> type) {
        if (isProfiledType(type)) {
            return CONCRETE_TYPE_MAP.get(type);
        }
        return type;
    }

    /**
     * @return the list of constraints for the modelClass or empty if there are none
     */
    public static List<Constraint> getConstraints(Class<?> modelClass) {
        return MODEL_CLASS_CONSTRAINT_MAP.getOrDefault(modelClass, Collections.emptyList());
    }

    /**
     * @return ElementInfo for the element with the passed name on the passed modelClass or null if the modelClass does
     *         not contain an element with this name
     */
    public static ElementInfo getElementInfo(Class<?> modelClass, String elementName) {
        return MODEL_CLASS_ELEMENT_INFO_MAP.getOrDefault(modelClass, Collections.emptyMap()).get(elementName);
    }

    /**
     * @return a collection of ElementInfo for all elements of the passed modelClass or empty if the class is not a FHIR
     *         model class
     */
    public static Collection<ElementInfo> getElementInfo(Class<?> modelClass) {
        return MODEL_CLASS_ELEMENT_INFO_MAP.getOrDefault(modelClass, Collections.emptyMap()).values();
    }

    /**
     * @return ElementInfo for the choice element with the passed typeSpecificElementName of the passed modelClass or
     *         null if the modelClass does not contain a choice element that can have this typeSpecificElementName
     */
    public static ElementInfo getChoiceElementInfo(Class<?> modelClass, String typeSpecificElementName) {
        for (ElementInfo elementInfo : getElementInfo(modelClass)) {
            if (elementInfo.isChoice() && elementInfo.getChoiceElementNames().contains(typeSpecificElementName)) {
                return elementInfo;
            }
        }
        return null;
    }

    /**
     * Get the actual element name from a Java field.
     */
    public static String getElementName(Field field) {
        return getElementName(field.getName());
    }

    /**
     * Get the actual element name from a Java field name.
     * This method reverses any encoding that was required to represent the FHIR element name in Java,
     * such as converting class to clazz.
     */
    public static String getElementName(String fieldName) {
        if ("clazz".equals(fieldName)) {
            return "class";
        }
        if (fieldName.startsWith("_")) {
            return fieldName.substring(1);
        }
        return fieldName;
    }

    /**
     * @return the set of element names for the passed modelClass or empty if it is not a FHIR model class
     * @implSpec choice type element names are returned without a type suffix; see {@link #getChoiceElementName(String,
     *           Class<?>)} for building the serialized name
     */
    public static Set<String> getElementNames(Class<?> modelClass) {
        return MODEL_CLASS_ELEMENT_INFO_MAP.getOrDefault(modelClass, Collections.emptyMap()).keySet();
    }

    /**
     * @return the model class for the element with name elementName on the passed modelClass or
     *         null if the passed modelClass does not have an element {@code elementName}
     */
    public static Class<?> getElementType(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.getType();
        }
        return null;
    }

    /**
     * Get the model class which declares the elementName found on the passed modelClass.
     *
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @return modelClass or a superclass of modelClass, or null if the element is not found on the passed modelClass
     */
    public static Class<?> getElementDeclaringType(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.getDeclaringType();
        }
        return null;
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @param type
     *            the model class to check
     * @return true if the passed modelClass contains an element with name elementName and the passed type is the one
     *         that declares it; otherwise false
     */
    public static boolean isElementDeclaredBy(Class<?> modelClass, String elementName, Class<?> type) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.isDeclaredBy(type);
        }
        return false;
    }

    private static Class<?> getFieldType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return field.getType();
    }

    /**
     * @return all model classes, including both resources and elements, concrete and abstract
     */
    public static Set<Class<?>> getModelClasses() {
        return MODEL_CLASS_ELEMENT_INFO_MAP.keySet();
    }

    /**
     * @param name
     *            the resource type name in titlecase to match the corresponding model class name
     * @return the model class that corresponds to the passed resource type name
     */
    public static Class<? extends Resource> getResourceType(String name) {
        return RESOURCE_TYPE_MAP.get(name);
    }

    /**
     * @return a collection of FHIR resource type model classes, including abstract supertypes
     */
    public static Collection<Class<? extends Resource>> getResourceTypes() {
        return RESOURCE_TYPE_MAP.values();
    }

    /**
     * @return a collection of FHIR resource type model classes
     */
    public static Collection<Class<? extends Resource>> getResourceTypes(boolean includeAbstractTypes) {
        if (includeAbstractTypes) {
            return RESOURCE_TYPE_MAP.values();
        } else {
            return CONCRETE_RESOURCE_TYPES;
        }
    }

    /**
     * @return the set of classes for the FHIR elements
     */
    public static Set<Class<? extends Element>> getDataTypes() {
        return DATA_TYPES;
    }

    /**
     * @return the name of the FHIR data type which corresponds to the passed type
     * @implNote primitive types will start with a lowercase letter,
     *           complex types and resources with an uppercaseletter
     */
    public static String getTypeName(Class<?> type) {
        String typeName = type.getSimpleName();
        if (Code.class.isAssignableFrom(type)) {
            typeName = "code";
        } else if (isPrimitiveType(type)) {
            typeName = typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
        }
        return typeName;
    }

    /**
     * @return the set of FHIR data type names for the passed modelClass and its supertypes
     * @implNote primitive types will start with a lowercase letter,
     *           complex types and resources with an uppercaseletter
     */
    public static Set<String> getTypeNames(Class<?> modelClass) {
        Set<String> typeNames = new HashSet<>();
        while (!Object.class.equals(modelClass)) {
            typeNames.add(getTypeName(modelClass));
            modelClass = modelClass.getSuperclass();
        }
        return typeNames;
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @return true if and only if {@code modelClass} is a BackboneElement
     */
    public static boolean isBackboneElementType(Class<?> modelClass) {
        return BackboneElement.class.isAssignableFrom(modelClass);
    }

    private static boolean isChoice(Field field) {
        return field.isAnnotationPresent(Choice.class);
    }

    private static boolean isReference(Field field) {
        return field.isAnnotationPresent(org.linuxforhealth.fhir.model.annotation.ReferenceTarget.class);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @return true if {@code modelClass} contains a choice element with name @{code elementName}; otherwise false
     */
    public static boolean isChoiceElement(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.isChoice();
        }
        return false;
    }

    /**
     * @param type
     *            a model class which represents a FHIR element
     * @return true if {@code type} is an allowed choice element type; otherwise false
     */
    public static boolean isChoiceElementType(Class<?> type) {
        return CHOICE_ELEMENT_TYPES.contains(type);
    }

    /**
     * @param type
     *            a model class which represents a FHIR element
     * @return true if {@code type} is subclass of org.linuxforhealth.fhir.model.type.Code; otherwise false
     */
    public static boolean isCodeSubtype(Class<?> type) {
        return Code.class.isAssignableFrom(type) && !Code.class.equals(type);
    }

    /**
     * @param modelObject
     *            a model object which represents a FHIR resource or element
     * @return true if {@code modelObject} is an element; otherwise false
     */
    public static boolean isElement(Object modelObject) {
        return (modelObject instanceof Element);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @return true if {@code modelClass} is an element; otherwise false
     */
    public static boolean isElementType(Class<?> modelClass) {
        return Element.class.isAssignableFrom(modelClass);
    }

    /**
     * @param type
     *            a model class which represents a FHIR element
     * @return true if {@code type} is a metadata type; otherwise false
     * @see <a href="https://www.hl7.org/fhir/R4/metadatatypes.html">https://www.hl7.org/fhir/R4/metadatatypes.html</a>
     */
    public static boolean isMetadataType(Class<?> type) {
        return ContactDetail.class.equals(type) ||
                Contributor.class.equals(type) ||
                DataRequirement.class.isAssignableFrom(type) ||
                RelatedArtifact.class.isAssignableFrom(type) ||
                UsageContext.class.equals(type) ||
                ParameterDefinition.class.equals(type) ||
                Expression.class.equals(type) ||
                TriggerDefinition.class.equals(type);
    }

    /**
     * @return true if {@code type} is a model class that represents a FHIR resource or element; otherwise false
     */
    public static boolean isModelClass(Class<?> type) {
        return isResourceType(type) || isElementType(type);
    }

    /**
     * @param type
     *            a model class which represents a FHIR element
     * @return true if {@code type} is a model class that represents a FHIR primitive type; otherwise false
     * @implNote xhtml is considered a primitive type
     * @see <a href="https://www.hl7.org/fhir/R4/datatypes.html#primitive">https://www.hl7.org/fhir/R4/datatypes.html#primitive</a>
     */
    public static boolean isPrimitiveType(Class<?> type) {
        return Base64Binary.class.equals(type) ||
            org.linuxforhealth.fhir.model.r4b.type.Boolean.class.equals(type) ||
            org.linuxforhealth.fhir.model.r4b.type.String.class.isAssignableFrom(type) ||
            Uri.class.isAssignableFrom(type) ||
            DateTime.class.equals(type) ||
            Date.class.equals(type) ||
            Time.class.equals(type) ||
            Instant.class.equals(type) ||
            org.linuxforhealth.fhir.model.r4b.type.Integer.class.isAssignableFrom(type) ||
            Decimal.class.equals(type) ||
            Xhtml.class.equals(type);
    }

    /**
     * @param type
     *            a model class which represents a FHIR element
     * @return true if {@code type} is a profiled data type; otherwise false
     */
    public static boolean isProfiledType(Class<?> type) {
        return CONCRETE_TYPE_MAP.containsKey(type);
    }

    private static boolean isRepeating(Field field) {
        return List.class.equals(field.getType());
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @return true if {@code modelClass} has an element {@code elementName} and it has max cardinality > 1;
     *         otherwise false
     */
    public static boolean isRepeatingElement(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.isRepeating();
        }
        return false;
    }

    private static boolean isRequired(Field field) {
        return field.isAnnotationPresent(Required.class);
    }

    private static boolean isSummary(Field field) {
        return field.isAnnotationPresent(Summary.class);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @return true if {@code modelClass} has an element {@code elementName} and it has min cardinality > 0;
     *         otherwise false
     */
    public static boolean isRequiredElement(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.isRequired();
        }
        return false;
    }

    /**
     * @param modelObject
     *            a model object which represents a FHIR resource or element
     * @return true if {@code modelObject} represents a FHIR resource; otherwise false
     */
    public static boolean isResource(Object modelObject) {
        return (modelObject instanceof Resource);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @return true if {@code modelClass} represents a FHIR resource; otherwise false
     */
    public static boolean isResourceType(Class<?> modelClass) {
        return Resource.class.isAssignableFrom(modelClass);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @return true if {@code modelClass} is an abstract FHIR model class; otherwise false
     */
    public static boolean isAbstract(Class<?> modelClass) {
        return Modifier.isAbstract(modelClass.getModifiers());
    }

    /**
     * @param name
     *            the resource type name in titlecase to match the corresponding model class name
     * @return true if {@code name} is a valid FHIR resource name; otherwise false
     * @implSpec this method returns true for abstract types like {@code Resource} and {@code DomainResource}
     */
    public static boolean isResourceType(String name) {
        return RESOURCE_TYPE_MAP.containsKey(name);
    }

    /**
     * @param name
     *            the resource type name in titlecase to match the corresponding model class name
     * @return true if {@code name} is a valid FHIR resource name; otherwise false
     * @implSpec this method returns false for abstract types like {@code Resource} and {@code DomainResource}
     */
    public static boolean isConcreteResourceType(String name) {
        Class<?> modelClass = RESOURCE_TYPE_MAP.get(name);
        return modelClass != null && !isAbstract(modelClass);
    }

    /**
     * @param modelClass
     *            a model class which represents a FHIR resource or element
     * @param elementName
     *            the name of the element; choice element names do not include a type suffix
     * @return true if {@code modelClass} has an element {@code elementName} and its marked as a summary element;
     *         otherwise false
     */
    public static boolean isSummaryElement(Class<?> modelClass, String elementName) {
        ElementInfo elementInfo = getElementInfo(modelClass, elementName);
        if (elementInfo != null) {
            return elementInfo.isSummary();
        }
        return false;
    }

    /**
     * @return a copy of the passed ZonedDateTime with the time truncated to {@code unit}
     */
    public static ZonedDateTime truncateTime(ZonedDateTime dateTime, ChronoUnit unit) {
        return dateTime == null ? null : dateTime.truncatedTo(unit);
    }

    /**
     * @return a copy of the passed LocalTime with the time truncated to {@code unit}
     */
    public static LocalTime truncateTime(LocalTime time, ChronoUnit unit) {
        return time == null ? null : time.truncatedTo(unit);
    }

    /**
     * @return a copy of the passed TemporalAccessor with the time truncated to {@code unit}
     */
    public static TemporalAccessor truncateTime(TemporalAccessor ta, ChronoUnit unit) {
        if (ta instanceof java.time.Instant) {
            ta = ((java.time.Instant) ta).truncatedTo(unit);
        } else if (ta instanceof ZonedDateTime) {
            ta = ((ZonedDateTime) ta).truncatedTo(unit);
        } else if (ta instanceof LocalDateTime) {
            ta = ((LocalDateTime) ta).truncatedTo(unit);
        } else if (ta instanceof LocalTime) {
            ta = ((LocalTime) ta).truncatedTo(unit);
        } else if (ta instanceof OffsetTime) {
            ta = ((OffsetTime) ta).truncatedTo(unit);
        } else if (ta instanceof OffsetDateTime) {
            ta = ((OffsetDateTime) ta).truncatedTo(unit);
        }

        return ta;
    }

    /**
     * @return true if {@code identifier} is a reserved keyword in FHIRPath version N1
     * @see <a href="http://hl7.org/fhirpath/2018Sep/index.html#keywords">http://hl7.org/fhirpath/2018Sep/index.html#keywords</a>
     */
    public static boolean isKeyword(String identifier) {
        return KEYWORDS.contains(identifier);
    }

    /**
     * Wraps the passed string identifier for use in FHIRPath
     * @see <a href="http://hl7.org/fhirpath/2018Sep/index.html#keywords">http://hl7.org/fhirpath/2018Sep/index.html#keywords</a>
     */
    public static String delimit(String identifier) {
        return String.format("`%s`", identifier);
    }

    /**
     * @return the implicit system for {@code code} if present, otherwise null
     */
    public static String getSystem(Code code) {
        if (code != null && code.getClass().isAnnotationPresent(org.linuxforhealth.fhir.model.annotation.System.class)) {
            return code.getClass().getAnnotation(org.linuxforhealth.fhir.model.annotation.System.class).value();
        }
        return null;
    }

    /**
     * @return the data type class associated with {@code typeName} parameter if exists, otherwise null
     */
    public static Class<?> getDataType(String typeName) {
        return DATA_TYPE_MAP.get(typeName);
    }

    public static boolean isCodeSubtype(String name) {
        return CODE_SUBTYPE_MAP.containsKey(name);
    }

    public static Collection<Class<?>> getCodeSubtypes() {
        return CODE_SUBTYPE_MAP.values();
    }
}
