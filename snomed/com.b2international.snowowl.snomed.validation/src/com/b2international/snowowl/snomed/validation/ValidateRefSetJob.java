/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.validation;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ValuedJob;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.validation.IClientSnomedComponentValidationService;
import com.b2international.snowowl.snomed.validation.diagnostic.SnomedRefSetDiagnostic;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * Reference set validation job (not EMF based). Currently there is only one
 * validation rule, we regard the member invalid if this is an <b>active</b>
 * member(s) but its referenced component is inactive.
 * <p>
 * The job also validates member SCT concepts by delegating to
 * {@link ValidateConceptsJob}.
 */
public class ValidateRefSetJob extends ValuedJob<Integer> {

	private static final String ACTIVE_MEMBER_INACTIVE_REFCOMPONENT = "The reference set member for component %s (%s) is active, however the referenced component itself is inactive";
	private static final String MISSING_REFERENCED_CONCEPT = "The reference set member is referring to a non-existing concept %s";
	private static final String MISSING_REFERENCED_DESCRIPTION = "The reference set member is referring to a non-existing description %s";
	private static final String MISSING_REFERENCED_RELATIONSHIP = "The reference set member is referring to a non-existing relationship %s";

	private final SnomedReferenceSet refSet;

	public ValidateRefSetJob(final String name, final Object family, final SnomedReferenceSet refSet) {
		super(name, family);
		this.refSet = refSet;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final List<SnomedReferenceSetMember> members = SnomedRequests
				.prepareSearchMember()
				.all()
				.filterByRefSet(refSet.getId())
				.setLocales(getLocales())
				.build(getBranch())
				.executeSync(getBus())
				.getItems();

		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Validating reference set members...", members.size());

		try {
			switch (refSet.getReferencedComponentType()) {
			case SnomedTerminologyComponentConstants.CONCEPT:
				return validateConceptMembers(members, subMonitor);
			case SnomedTerminologyComponentConstants.DESCRIPTION:
				return validateDescriptionMembers(subMonitor, members);
			case SnomedTerminologyComponentConstants.RELATIONSHIP:
				return validateRelationshipMembers(subMonitor, members);
			default:
				// TODO: This component type cannot be validated here. What to do?
				return Status.OK_STATUS;
			}
		} finally {
			monitor.done();
		}
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}

	private String getBranch() {
		return BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath();
	}

	private IEventBus getBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private IStatus validateConceptMembers(final Collection<SnomedReferenceSetMember> members, final SubMonitor subMonitor) {
		// We will also validate the concepts themselves; double up the tick count
		subMonitor.setWorkRemaining(2 * members.size());

		final List<ISnomedConcept> referencedConcepts = SnomedRequests
				.prepareSearchConcept()
				.all()
				.setComponentIds(getReferencedComponentIds(members))
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(getBranch())
				.executeSync(getBus())
				.getItems();

		final ImmutableMap<String, ISnomedConcept> referencedConceptsMap = asMap(referencedConcepts);

		final List<IDiagnostic> diagnostics = newArrayList();
		final Set<ISnomedConcept> uniqueConcepts = newHashSet();

		for (final SnomedReferenceSetMember member : members) {
			final ISnomedConcept referencedConcept = referencedConceptsMap.get(member.getReferencedComponent().getId());

			if (null == referencedConcept) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						MISSING_REFERENCED_CONCEPT, 
						member.getReferencedComponent().getId())));

				continue;
			}

			if (uniqueConcepts.add(referencedConcept)) {
				if (!referencedConcept.isActive() && member.isActive()) {
					diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
							ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
							getLabel(referencedConcept), 
							member.getReferencedComponent().getId())));
				}
			}

			if (subMonitor.isCanceled()) {
				break;
			}

			subMonitor.worked(1);
		}

		int errorCount = 0;

		if (!subMonitor.isCanceled()) {
			// Run concept validation for members here
			final IClientSnomedComponentValidationService validationService = ApplicationContext
					.getServiceForClass(IClientSnomedComponentValidationService.class);
			final ValidateConceptsJob<SnomedConceptDocument, String> validateConceptsJob = new ValidateConceptsJob<SnomedConceptDocument, String>(
					"", family, SnomedConceptDocument.fromConcepts(uniqueConcepts), validationService);

			subMonitor.setWorkRemaining(uniqueConcepts.size());
			validateConceptsJob.run(subMonitor.newChild(uniqueConcepts.size()));

			errorCount += validateConceptsJob.getValue();
		}

		return finishValidation(subMonitor, diagnostics, errorCount);
	}

	private Collection<String> getReferencedComponentIds(final Collection<SnomedReferenceSetMember> members) {
		return FluentIterable.from(members).transform(new Function<SnomedReferenceSetMember, String>() {
			@Override
			public String apply(SnomedReferenceSetMember input) {
				return input.getReferencedComponent().getId();
			}
		}).toList();
	}

	private <C extends SnomedCoreComponent> ImmutableMap<String, C> asMap(final Collection<C> components) {
		return FluentIterable.from(components).uniqueIndex(new Function<C, String>() {
			@Override
			public String apply(C input) {
				return input.getId();
			}
		});
	}

	private IStatus validateDescriptionMembers(final SubMonitor subMonitor, final List<SnomedReferenceSetMember> members) {
		final List<IDiagnostic> diagnostics = newArrayList();

		final List<ISnomedDescription> referencedDescriptions = SnomedRequests
				.prepareSearchDescription()
				.all()
				.setComponentIds(getReferencedComponentIds(members))
				.setLocales(getLocales())
				.build(getBranch())
				.executeSync(getBus())
				.getItems();

		final ImmutableMap<String, ISnomedDescription> referencedDescriptionsMap = asMap(referencedDescriptions);

		for (final SnomedReferenceSetMember member : members) {
			final ISnomedDescription referencedDescription = referencedDescriptionsMap.get(member.getReferencedComponent().getId());

			if (null == referencedDescription) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						MISSING_REFERENCED_DESCRIPTION, 
						member.getReferencedComponent().getId())));

				continue;
			}

			if (!referencedDescription.isActive() && member.isActive()) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
						referencedDescription.getTerm(), 
						member.getReferencedComponent().getId())));
			}

			if (subMonitor.isCanceled()) {
				break;
			}

			subMonitor.worked(1);
		}

		return finishValidation(subMonitor, diagnostics);
	}

	private IStatus validateRelationshipMembers(final SubMonitor subMonitor, final List<SnomedReferenceSetMember> members) {
		final List<IDiagnostic> diagnostics = newArrayList();

		final List<ISnomedRelationship> referencedRelationships = SnomedRequests
				.prepareSearchRelationship()
				.all()
				.setComponentIds(getReferencedComponentIds(members))
				.setExpand("source(expand(pt())),type(expand(pt())),destination(expand(pt()))")
				.setLocales(getLocales())
				.build(getBranch())
				.executeSync(getBus())
				.getItems();

		final ImmutableMap<String, ISnomedRelationship> referencedRelationshipsMap = asMap(referencedRelationships);

		for (final SnomedReferenceSetMember member : members) {
			final ISnomedRelationship referencedRelationship = referencedRelationshipsMap.get(member.getReferencedComponent().getId());

			if (null == referencedRelationship) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						MISSING_REFERENCED_RELATIONSHIP, 
						member.getReferencedComponent().getId())));

				continue;
			}

			if (!referencedRelationship.isActive() && member.isActive()) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
						getLabel(referencedRelationship), 
						member.getReferencedComponent().getId())));
			}

			if (subMonitor.isCanceled()) {
				break;
			}

			subMonitor.worked(1);
		}

		return finishValidation(subMonitor, diagnostics);
	}

	private String getLabel(final ISnomedRelationship relationship) {
		return getLabel(relationship.getSourceConcept()) + " " + getLabel(relationship.getTypeConcept()) + " "
				+ getLabel(relationship.getDestinationConcept());
	}

	private String getLabel(final ISnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}

	private IStatus finishValidation(final SubMonitor subMonitor, final List<IDiagnostic> errors) {
		return finishValidation(subMonitor, errors, 0);
	}

	private IStatus finishValidation(final SubMonitor subMonitor, final List<IDiagnostic> errors, int errorCount) {

		if (!errors.isEmpty()) {
			errorCount += errors.size();

			final MarkerManager markerManager = ApplicationContext.getServiceForClass(MarkerManager.class);
			final SnomedRefSetDiagnostic summaryDiagnostic = new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, "", errors);
			markerManager.createValidationMarkerOnComponent(refSet, summaryDiagnostic);
		}

		setValue(errorCount);
		return subMonitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
	}
	
}
