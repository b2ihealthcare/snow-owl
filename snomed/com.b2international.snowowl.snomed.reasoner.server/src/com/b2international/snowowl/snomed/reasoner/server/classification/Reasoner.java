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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator.CONSTRAINED_HEAP;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.AbstractDisposableService;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.oplock.OperationLockRunner;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerException;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;
import com.b2international.snowowl.snomed.reasoner.server.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.server.ontology.SnomedOntologyService;
import com.google.common.base.Stopwatch;

/**
 */
public class Reasoner extends AbstractDisposableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Reasoner.class);
	
	private final String reasonerId;
	private final IBranchPath branchPath;
	private final boolean shared;

	private final ReasonerStateMachine stateMachine = new ReasonerStateMachine(ReasonerState.UNLOADED);
	private final AtomicReference<InitialReasonerTaxonomyBuilder> taxonomyBuilder = new AtomicReference<>();
	
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	
	public Reasoner(final String reasonerId, final IBranchPath branchPath, final boolean shared) {
		this.reasonerId = reasonerId;
		this.branchPath = branchPath;
		this.shared = shared;
	}

	public IBranchPath getBranchPath() {
		return branchPath;
	}

	public String getReasonerId() {
		return reasonerId;
	}

	private OWLReasoner getOrCreateReasoner(final List<ConceptDefinition> additionalDefinitions) {
		if (null == reasoner) {
			final OWLReasonerConfiguration configuration = new SimpleConfiguration(new ConsoleProgressMonitor());
			final ProtegeOWLReasonerInfo reasonerInfo = getReasonerPreferencesService().createReasonerInfo(reasonerId);
			final OWLReasonerFactory reasonerFactory = reasonerInfo.getReasonerFactory();
			reasoner = reasonerFactory.createReasoner(createOntology(additionalDefinitions), configuration);
		}
		
		return reasoner;
	}

	private OWLOntology createOntology(final List<ConceptDefinition> additionalDefinitions) {
		checkState(!shared || additionalDefinitions.isEmpty());
		
		try {
			final SnomedOntologyService ontologyService = getSnomedOntologyService();
			ontology = ontologyService.createOntology(branchPath, shared);
			
			for (final ConceptDefinition conceptDefinition : additionalDefinitions) {
				ontologyService.applyChanges(ontology, conceptDefinition.add(ontology));
			}
			
			// must happen before any unload
			if (ontology instanceof DelegateOntology) {
				DelegateOntology delegateOntology = (DelegateOntology) ontology;
				taxonomyBuilder.set(delegateOntology.getReasonerTaxonomyBuilder());
			}
			
			return ontology;
		} catch (final OWLOntologyCreationException e) {
			throw new ReasonerException(e);
		}
	}

	public ReasonerTaxonomy classify(final String userId, final String parentContextDescription, final List<ConceptDefinition> additionalDefinitions) {
		
		try {
			
			if (CONSTRAINED_HEAP) {
				getApplicationContext().getService(ICDORepositoryManager.class).clearRevisionCache();
				System.gc();
			}

			final Stopwatch stopwatch = Stopwatch.createStarted();
			
			if (!isSynchronized()) {
				classifyWithContext(createLockContext(userId, parentContextDescription), 5000L, additionalDefinitions);
			}
					
			final ReasonerTaxonomy taxonomy = computeTaxonomy(stopwatch);
			stopwatch.stop();
			SnomedReasonerServerActivator.logInfo(MessageFormat.format("Classified ontology in {0}.", TimeUtil.toString(stopwatch)));
		
			if (CONSTRAINED_HEAP) {
				unload();
				stateMachine.unload();
			}
			
			return taxonomy;
			
		} catch (final RuntimeException | InvocationTargetException | InterruptedException | OutOfMemoryError e) {
			LOGGER.error(MessageFormat.format("Caught exception while classifying ontology on branch path ''{0}''.", branchPath), e);
			
			try {
				unload();
			} catch (final Exception suppressed) {
				e.addSuppressed(suppressed);
			}
			
			stateMachine.fail();
			throw SnowowlRuntimeException.wrap(e);
		}
	}

	private void classifyWithContext(final DatastoreLockContext lockContext, final long timeoutMillis, final List<ConceptDefinition> additionalDefinitions) throws InterruptedException, InvocationTargetException {
		
		OperationLockRunner.with(getDatastoreOperationLockManager()).run(new Runnable() { @Override public void run() {
			classifyLocked(additionalDefinitions);
		}}, lockContext, timeoutMillis, createLockTarget());
	}

	private boolean isSynchronized() {
		return getState().oneOf(ReasonerState.SYNCHRONIZED);
	}

	private void classifyLocked(final List<ConceptDefinition> additionalDefinitions) {
		stateMachine.beginClassification();

		final OWLReasoner reasoner = getOrCreateReasoner(additionalDefinitions);
		reasoner.flush();
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		stateMachine.endClassification();
	}

	private DatastoreLockContext createLockContext(final String userId, final String parentDescription) {
		return new DatastoreLockContext(userId, DatastoreLockContextDescriptions.CLASSIFY, parentDescription);
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget() {
		return new SingleRepositoryAndBranchLockTarget(getRepositoryUuid(), branchPath);
	}

	private String getRepositoryUuid() {
		return getConnectionManager().get(SnomedPackage.eINSTANCE).getUuid();
	}

	private ReasonerTaxonomy computeTaxonomy(final Stopwatch stopwatch) {
		final ReasonerTaxonomy reasonerTaxonomy = new ReasonerTaxonomy(branchPath, stopwatch.elapsed(TimeUnit.MILLISECONDS));
		new ReasonerTaxonomyWalker(branchPath, reasoner, reasonerTaxonomy).walk();
		return reasonerTaxonomy;
	}

	private ReasonerState getState() {
		return stateMachine.getState();
	}
	
	public AtomicReference<InitialReasonerTaxonomyBuilder> getTaxonomyBuilder() {
		return taxonomyBuilder;
	}

	public void setStale() {

		try {
			stateMachine.setStale();
		} catch (final Exception e) {
			LOGGER.error(MessageFormat.format("Caught exception while marking reasoner as stale on branch path ''{0}''.", branchPath), e);
			stateMachine.fail();
		}
	}
	
	@Override
	protected void onDispose() {
		
		try {
			unload();
			stateMachine.unload();
		} catch (final Exception e) {
			LOGGER.error(MessageFormat.format("Caught exception while retiring reasoner for branch path ''{0}''.", branchPath), e);
			stateMachine.fail();
		}
		
		super.onDispose();
	}

	private void unload() {
		
		if (null != reasoner) {
			reasoner.dispose();
			reasoner = null;
			
			final SnomedOntologyService ontologyService = getSnomedOntologyService();
			ontologyService.removeOntology(ontology);
			ontology = null;
		}
	}

	@Override
	public String toString() {
		return "ReasonerServiceWrapper[branchPath=" + branchPath + "]";
	}

	private ICDOConnectionManager getConnectionManager() {
		return getApplicationContext().getService(ICDOConnectionManager.class);
	}

	private IDatastoreOperationLockManager getDatastoreOperationLockManager() {
		return getApplicationContext().getService(IDatastoreOperationLockManager.class);
	}
	
	private IReasonerPreferencesService getReasonerPreferencesService() {
		return getApplicationContext().getService(IReasonerPreferencesService.class);
	}

	private SnomedOntologyService getSnomedOntologyService() {
		return getApplicationContext().getService(SnomedOntologyService.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
}