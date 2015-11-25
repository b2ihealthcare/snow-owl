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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ValuedJob;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.validation.IClientSnomedComponentValidationService;
import com.b2international.snowowl.snomed.validation.diagnostic.SnomedRefSetDiagnostic;

/**
 * Reference set validation job (not EMF based). Currently there is only one validation rule, we regard the member invalid if this is an <b>active</b> member(s)
 * but its referenced component is inactive.
 * <p>
 * The job also validates member SCT concepts by delegating to {@link ValidateConceptsJob}.
 */
public class ValidateRefSetJob extends ValuedJob<Integer> {

	private final SnomedRefSetIndexEntry refSetEntry;
	
	public ValidateRefSetJob(final String name, final Object family, final SnomedRefSetIndexEntry refSetEntry) {
		super(name, family);
		this.refSetEntry = refSetEntry;
	}
	
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		
		final SnomedClientIndexService indexService = ApplicationContext.getServiceForClass(SnomedClientIndexService.class);
		final SnomedRefSetMemberIndexQueryAdapter refSetMemberQueryAdapter = new SnomedRefSetMemberIndexQueryAdapter(refSetEntry.getId(), "", false);
		final List<SnomedRefSetMemberIndexEntry> members = indexService.search(refSetMemberQueryAdapter);

		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Validating reference set members...", members.size());

		try {
			switch (refSetEntry.getReferencedComponentType()) {
				case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
					return validateConceptMembers(members, subMonitor);
				case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
					return validateDescriptionMembers(subMonitor, members);
				case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
					return validateRelationshipMembers(subMonitor, members);
				default:
					// TODO: This component type cannot be validated here. What to do?
					return Status.OK_STATUS;
			}
		} finally {
			monitor.done();
		}
	}

	private IStatus validateConceptMembers(final Collection<SnomedRefSetMemberIndexEntry> members, final SubMonitor subMonitor) {
		
		// We will also validate the concepts themselves; double up the tick count
		subMonitor.setWorkRemaining(2 * members.size());
		
		final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedClientTerminologyBrowser.class);
		final List<IDiagnostic> diagnostics = newArrayList();
		final Set<SnomedConceptIndexEntry> uniqueConcepts = newHashSet();
		
		for (final SnomedRefSetMemberIndexEntry memberIndexEntry : members) {
			final SnomedConceptIndexEntry referencedConcept = terminologyBrowser.getConcept(memberIndexEntry.getReferencedComponentId());
			
			if (null == referencedConcept) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						SnomedRefSetDiagnostic.MISSING_REFERENCED_CONCEPT, 
						memberIndexEntry.getLabel(),
						memberIndexEntry.getReferencedComponentId())));
				
				continue;
			} 
			
			if (uniqueConcepts.add(referencedConcept)) {
				if (!referencedConcept.isActive() && memberIndexEntry.isActive()) {
					diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
							SnomedRefSetDiagnostic.ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
							memberIndexEntry.getLabel(),
							memberIndexEntry.getReferencedComponentId())));
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
			final IClientSnomedComponentValidationService validationService = ApplicationContext.getServiceForClass(IClientSnomedComponentValidationService.class);
			final ValidateConceptsJob<SnomedConceptIndexEntry, String> validateConceptsJob = new ValidateConceptsJob<SnomedConceptIndexEntry, String>("", 
					family, 
					uniqueConcepts,
					validationService);
			
			subMonitor.setWorkRemaining(uniqueConcepts.size());
			validateConceptsJob.run(subMonitor.newChild(uniqueConcepts.size()));
			
			errorCount += validateConceptsJob.getValue();
		}
		
		return finishValidation(subMonitor, diagnostics, errorCount);
	}

	private IStatus validateDescriptionMembers(final SubMonitor subMonitor, final List<SnomedRefSetMemberIndexEntry> members) {
		
		final SnomedDescriptionLookupService lookupService = new SnomedDescriptionLookupService();
		final List<IDiagnostic> diagnostics = newArrayList();
		
		for (final SnomedRefSetMemberIndexEntry memberIndexEntry : members) {
			final SnomedDescriptionIndexEntry referencedDescription = (SnomedDescriptionIndexEntry) lookupService.getComponent(memberIndexEntry.getReferencedComponentId());
			
			if (null == referencedDescription) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						SnomedRefSetDiagnostic.MISSING_REFERENCED_DESCRIPTION, 
						memberIndexEntry.getLabel(),
						memberIndexEntry.getReferencedComponentId())));
				
				continue;
			} 
			
			if (!referencedDescription.isActive() && memberIndexEntry.isActive()) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						SnomedRefSetDiagnostic.ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
						memberIndexEntry.getLabel(),
						memberIndexEntry.getReferencedComponentId())));
			}
			
			if (subMonitor.isCanceled()) {
				break;
			}
			
			subMonitor.worked(1);
		}

		return finishValidation(subMonitor, diagnostics);
	}

	private IStatus validateRelationshipMembers(final SubMonitor subMonitor, final List<SnomedRefSetMemberIndexEntry> members) {
		
		final SnomedClientStatementBrowser statementBrowser = ApplicationContext.getServiceForClass(SnomedClientStatementBrowser.class);
		final List<IDiagnostic> diagnostics = newArrayList();
		
		for (final SnomedRefSetMemberIndexEntry memberIndexEntry : members) {
			final SnomedRelationshipIndexEntry referencedRelationship = statementBrowser.getStatement(memberIndexEntry.getReferencedComponentId());
			
			if (null == referencedRelationship) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						SnomedRefSetDiagnostic.MISSING_REFERENCED_RELATIONSHIP, 
						memberIndexEntry.getLabel(),
						memberIndexEntry.getReferencedComponentId())));
				
				continue;
			} 
			
			if (!referencedRelationship.isActive() && memberIndexEntry.isActive()) {
				diagnostics.add(new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, String.format(
						SnomedRefSetDiagnostic.ACTIVE_MEMBER_INACTIVE_REFCOMPONENT,
						memberIndexEntry.getLabel(),
						memberIndexEntry.getReferencedComponentId())));
			}
			
			if (subMonitor.isCanceled()) {
				break;
			}
			
			subMonitor.worked(1);
		}

		return finishValidation(subMonitor, diagnostics);
	}

	private IStatus finishValidation(final SubMonitor subMonitor, final List<IDiagnostic> errors) {
		return finishValidation(subMonitor, errors, 0);
	}
	
	private IStatus finishValidation(final SubMonitor subMonitor, final List<IDiagnostic> errors, int errorCount) {
		
		if (!errors.isEmpty()) {
			errorCount += errors.size();
			
			final MarkerManager markerManager = ApplicationContext.getServiceForClass(MarkerManager.class);
			final SnomedRefSetDiagnostic summaryDiagnostic = new SnomedRefSetDiagnostic(DiagnosticSeverity.ERROR, "", errors);
			markerManager.createValidationMarkerOnComponent(refSetEntry, summaryDiagnostic);
		}
		
		setValue(errorCount);
		return subMonitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
	}
}