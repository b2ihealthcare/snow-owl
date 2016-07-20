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
package com.b2international.snowowl.snomed.refset.derivation;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

/**
 * Abstract class to derive SNOMED&nbsp;CT simple type and simple map reference sets.
 * 
 * @since Snow&nbsp;owl 3.0.1
 */
public abstract class AbstractSnomedRefSetDerivator {
	
	protected static final int COMMIT_INTERVAL = 100000;
	
	private final String refSetId;
	private final String refSetName;
	private final String moduleId;
	private final boolean mapTarget;

	private boolean isCanceled; 
	private String commitMessage;
	private Set<String> conceptIds;

	private final SnomedEditingContext context;

	public AbstractSnomedRefSetDerivator(final String refSetId, final String refSetName, final boolean mapTarget) {
		this.refSetId = refSetId;
		this.refSetName = refSetName;
		this.mapTarget = mapTarget;
		this.context = new SnomedEditingContext();
		this.moduleId = context.getDefaultModuleConcept().getId();
	}
	
	/**
	 * Runs the derive process.
	 * 
	 * @param parentMonitor the monitor for the process.
	 * @param commitMessage the message of the commit.
	 * @throws SnowowlServiceException
	 */
	public void run(final IProgressMonitor parentMonitor, final String commitMessage) throws SnowowlServiceException {
		
		this.commitMessage = commitMessage;
		
		final SubMonitor monitor = SubMonitor.convert(parentMonitor, getTotalWork());
		monitor.worked(1);
		
		collectRefSetMembers(monitor);
		isCanceled(monitor);
		
		if (!isCanceled) {
			deriveComponents(monitor);
		}
		
		monitor.done();
		
		dispose();
	}

	private void isCanceled(final SubMonitor monitor) {
		if (monitor.isCanceled()) {
			isCanceled = true;
		}
	}

	/**
	 * Gets the total work for the monitor based on the derive type.
	 * 
	 * @return the total work.
	 */
	protected abstract int getTotalWork();
	
	/**
	 * Derive the components based on the selected refset and the derive type.
	 * 
	 * @param monitor the monitor for the derive process.
	 * @throws SnowowlServiceException
	 */
	protected abstract void deriveComponents(final SubMonitor monitor) throws SnowowlServiceException;
	
	/**
	 * Derives the concepts from the reference set members.
	 * 
	 * @param parentMonitor
	 * @throws SnowowlServiceException
	 */
	protected void deriveConcepts(final SubMonitor parentMonitor) throws SnowowlServiceException {
		
		final SubMonitor monitor = parentMonitor.newChild(conceptIds.size());
		monitor.setTaskName("Creating concept simple type reference set...");
		
		final Set<SnomedRefSetMember> refSetMembers = Sets.newHashSet();
		final SnomedRegularRefSet refSet = createSimpleTypeRefSet("", CONCEPT);
		
		for (final String conceptId : conceptIds) {
			
			if (isCanceled) {
				return;
			}
			
			final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(CONCEPT, conceptId);
			final SnomedRefSetMember refSetMember = context.getRefSetEditingContext().createSimpleTypeRefSetMember(identifierPair, moduleId, refSet);
			
			refSetMembers.add(refSetMember);

			monitor.worked(1);
			isCanceled(monitor);
			
		}
		
		refSet.getMembers().addAll(refSetMembers);
		commit(parentMonitor);
	}
	
	/**
	 * Derives the descriptions from the reference set members.
	 * 
	 * @param parentMonitor
	 * @throws SnowowlServiceException
	 */
	protected void deriveDescriptions(final SubMonitor parentMonitor) throws SnowowlServiceException {
		
		final SubMonitor monitor = parentMonitor.newChild(1);
		monitor.setTaskName("Creating description simple type reference set...");
		monitor.setWorkRemaining(conceptIds.size());
		
		final Set<SnomedRefSetMember> refSetMembers = Sets.newHashSet();
		final SnomedRegularRefSet refSet = createSimpleTypeRefSet(" - descriptions", DESCRIPTION);
		
		for (ISnomedDescription description : getDescriptions(conceptIds)) {
			if (isCanceled) {
				return;
			}
			
			final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(DESCRIPTION, description.getId());
			final SnomedRefSetMember refSetMember = context.getRefSetEditingContext().createSimpleTypeRefSetMember(identifierPair, moduleId, refSet);
			
			refSetMembers.add(refSetMember);
			
			monitor.worked(1);
			isCanceled(monitor);
		}
		
		refSet.getMembers().addAll(refSetMembers);
		commit(parentMonitor);
	}
	
	private SnomedDescriptions getDescriptions(Collection<String> conceptIds) {
		return SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
			.filterByConceptId(conceptIds)
			.build(context.getBranch())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
	}

	/**
	 * Derives the relationships from the reference set members.
	 * 
	 * @param parentMonitor
	 * @throws SnowowlServiceException
	 */
	protected void deriveRelationships(final SubMonitor parentMonitor) throws SnowowlServiceException {
		final SubMonitor monitor = parentMonitor.newChild(1);
		monitor.setTaskName("Creating relationship simple type reference set...");
		
		final SnomedRegularRefSet refSet = createSimpleTypeRefSet(" - relationships", RELATIONSHIP);
		
		final SnomedRelationships outboundRelationships = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterBySource(conceptIds)
				.build(context.getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		
		final Set<SnomedRefSetMember> refSetMembers = Sets.newHashSet();
		monitor.setWorkRemaining(outboundRelationships.getTotal());
		for (ISnomedRelationship relationship : outboundRelationships) {
			isCanceled(monitor);
			if (isCanceled) {
				return;
			}
			if (conceptIds.contains(relationship.getDestinationId())) {
				final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(RELATIONSHIP, relationship.getId());
				final SnomedRefSetMember refSetMember = context.getRefSetEditingContext().createSimpleTypeRefSetMember(identifierPair, moduleId, refSet);
				refSetMembers.add(refSetMember);
			}
			monitor.worked(1);
		}
		
		refSet.getMembers().addAll(refSetMembers);
		monitor.done();
		commit(parentMonitor);
	}
	
	public Set<String> getConceptIds() {
		return conceptIds;
	}
	
	/*
	 * Disposes the editing context.
	 */
	private void dispose() {
		if (null != context) {
			context.close();
		}
	}
	
	/*
	 * Commits the changes.
	 */
	private void commit(final SubMonitor parentMonitor) throws SnowowlServiceException {
		if (!isCanceled) {
			final SubMonitor monitor = parentMonitor.newChild(1);
			monitor.setTaskName("Committing changes...");
			context.commit(commitMessage);
			monitor.worked(1);
		}
	}
	
	/*
	 * Creates a simple type reference set with the given referenced component type.
	 */
	private SnomedRegularRefSet createSimpleTypeRefSet(final String postName, final String referencedComponentType) {
		return context.getRefSetEditingContext().createSnomedSimpleTypeRefSet(MessageFormat.format("{0}{1}", refSetName, postName), referencedComponentType);
	}
	
	/*
	 * Collects the reference set members based on the reference set ID.
	 */
	private void collectRefSetMembers(final SubMonitor monitor) {
		monitor.setTaskName("Collecting reference set members...");

		conceptIds = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByRefSet(refSetId)
				// TODO filter referencedComponent/map target by active flag
				.build(context.getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedReferenceSetMembers, Set<String>>() {
					@Override
					public Set<String> apply(SnomedReferenceSetMembers input) {
						return FluentIterable.from(input).transform(new Function<SnomedReferenceSetMember, String>() {
							@Override
							public String apply(SnomedReferenceSetMember input) {
								if (mapTarget) {
									return (String) input.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET);
								} else {
									return input.getReferencedComponent().getId();
								}
							}
						}).toSet();
					}
				})
				.getSync();
		
		monitor.worked(1);
	}

}