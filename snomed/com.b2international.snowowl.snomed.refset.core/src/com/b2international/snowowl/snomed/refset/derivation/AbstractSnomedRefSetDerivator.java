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
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.BooleanQuery;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import bak.pcj.map.LongKeyMap;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionContainerQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
	private final SnomedClientIndexService indexService;

	public AbstractSnomedRefSetDerivator(final String refSetId, final String refSetName, final boolean mapTarget) {
		this.refSetId = refSetId;
		this.refSetName = refSetName;
		this.mapTarget = mapTarget;
		this.context = new SnomedEditingContext();
		this.moduleId = context.getDefaultModuleConcept().getId();
		this.indexService = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
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
		
		final Iterable<List<String>> partitions = Iterables.partition(conceptIds, BooleanQuery.getMaxClauseCount());
		
		for (final List<String> partition : partitions) {
			
			final IIndexQueryAdapter<SnomedDescriptionIndexEntry> adapter = createQueryDescriptionAdapter(Lists.newArrayList(partition));
			
			for (final String descriptionId : searchForDescriptionIds(adapter)) {
				
				if (isCanceled) {
					return;
				}
				
				final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(DESCRIPTION, descriptionId);
				final SnomedRefSetMember refSetMember = context.getRefSetEditingContext().createSimpleTypeRefSetMember(identifierPair, moduleId, refSet);
				
				refSetMembers.add(refSetMember);
				
				monitor.worked(1);
				isCanceled(monitor);
				
			}
		}
		
		refSet.getMembers().addAll(refSetMembers);
		commit(parentMonitor);
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
		monitor.setWorkRemaining(conceptIds.size());
		
		final Set<SnomedRefSetMember> refSetMembers = Sets.newHashSet();
		final SnomedRegularRefSet refSet = createSimpleTypeRefSet(" - relationships", RELATIONSHIP);
		
		final SnomedClientStatementBrowser browser = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class);
		final LongKeyMap activeStatements = browser.getAllActiveStatements();
		
		for (final String conceptId : conceptIds) {
			
			if (isCanceled) {
				return;
			}

			Object object = activeStatements.get(Long.parseLong(conceptId));

			if (object instanceof List) {

				@SuppressWarnings("unchecked")
				final List<StatementFragment> fragments = (List<StatementFragment>) object;

				for (final StatementFragment fragment : fragments) {

					if (conceptIds.contains(Long.toString(fragment.getDestinationId()))) {

						final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.<String>create(RELATIONSHIP, Long.toString(fragment.getStatementId()));
						final SnomedRefSetMember refSetMember = context.getRefSetEditingContext().createSimpleTypeRefSetMember(identifierPair, moduleId, refSet);

						refSetMembers.add(refSetMember);
					}
				}
			}

			monitor.worked(1);
			isCanceled(monitor);
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
	 * Creates a query for querying descriptions.
	 */
	private SnomedDescriptionContainerQueryAdapter createQueryDescriptionAdapter(final Collection<String> componentIds) {
		return SnomedDescriptionContainerQueryAdapter.createFindByConceptIds(componentIds);
	}
	
	/*
	 * Gets the descriptions IDs.
	 */
	private Collection<String> searchForDescriptionIds(final IIndexQueryAdapter<SnomedDescriptionIndexEntry> adapter) {
		return getIndexService().searchUnsortedIds(adapter);
	}
	
	/*
	 * Gets the index service.
	 */
	private SnomedClientIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
	}
	
	/*
	 * Collects the reference set members based on the reference set ID.
	 */
	private void collectRefSetMembers(final SubMonitor monitor) {
		monitor.setTaskName("Collecting reference set members...");
		
		final SnomedRefSetMemberIndexQueryAdapter queryAdapter = new SnomedRefSetMemberIndexQueryAdapter(refSetId, "", true);
		
		final Set<String> ids = Sets.newHashSet(Collections2.transform(indexService.search(queryAdapter), new Function<SnomedRefSetMemberIndexEntry, String>() {
			@Override
			public String apply(SnomedRefSetMemberIndexEntry entry) {
				if (mapTarget) {
					return entry.getMapTargetComponentId();
				} else {
					return entry.getReferencedComponentId();
				}
			}
		}));
		
		final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> browser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		
		isCanceled(monitor);
		if (isCanceled) {
			return;
		}
		
		conceptIds =  Sets.newHashSet(Iterables.filter(ids, new Predicate<String>() {
			@Override
			public boolean apply(final String conceptId) {
				final SnomedConceptIndexEntry concept = browser.getConcept(conceptId);

				if (null != concept && concept.isActive()) {
					return true;
				} else {
					return false;
				}
			}
		}));
		
		monitor.worked(1);
	}

}