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
package com.b2international.snowowl.datastore.server.index;

import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.AbstractCDOChangeProcessor;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.collect.Iterables;

/**
 * An abstract {@link ICDOChangeProcessor} implementation which makes changes to an index.
 */
public abstract class AbstractIndexCDOChangeProcessor<E extends IIndexEntry, T extends EObject> extends AbstractCDOChangeProcessor<E, T> {

	public AbstractIndexCDOChangeProcessor(IIndexUpdater<E> indexService, IBranchPath branchPath, Collection<EClass> trackedEClasses) {
		super(indexService, branchPath, trackedEClasses);
	}

	/**
	 * Indexes the dirty component argument.
	 * @param dirtyComponent the changed component.
	 */
	@Override
	protected void processUpdate(final T dirtyComponent) {
		indexService.index(branchPath, createMappingStrategyForDirtyComponent(dirtyComponent));
	}

	/**
	 * Indexes the new component argument.
	 * @param newComponent the new component to index.
	 */
	@Override
	protected void processAddition(final T newComponent) {
		indexService.index(branchPath, createMappingStrategyForNewComponent(newComponent));
	}

	/**
	 * Indexes a deletion defined with the {@link CDOID} and {@link EClass} entry argument.
	 * @param detachedComponent the entry representing a deleted component to index.
	 */
	@Override
	protected void processDeletion(final Entry<CDOID, EClass> detachedComponent) {
		indexService.delete(branchPath, CDOIDUtil.getLong(detachedComponent.getKey()));
	}
	
	@Override
	public void rollback() throws SnowowlServiceException {
		indexService.rollback(branchPath);
		super.rollback();
	}
	
	@Override
	public void commit() throws SnowowlServiceException {
		indexService.commit(branchPath);
		super.commit();
	}
	
	@Override
	protected void populateChangedComponents() {
		// XXX: This subclass may modify the change set before it is processed and committed to the database
		for (final CodeSystemVersion dirtyCodeSystemVersion : Iterables.filter(commitChangeSet.getDirtyComponents(), CodeSystemVersion.class)) {
			checkAndSetCodeSystemLastUpdateTime(dirtyCodeSystemVersion);
		}
		
		super.populateChangedComponents();
	}

	/**
	 * Create a {@link IIndexMappingStrategy} for the specified object.
	 * @param object
	 * @param <C> - type of the terminology independent component
	 * @return
	 */
	protected abstract <C extends IComponent<?>> IIndexMappingStrategy createMappingStrategy(T object);

	/**
	 * Creates the index mapping strategy for the new component.
	 * @param newComponent the new component to index.
	 * @return the {@link IIndexMappingStrategy index mapping strategy} for the new component.
	 */
	protected IIndexMappingStrategy createMappingStrategyForNewComponent(final T newComponent) {
		return createMappingStrategy(newComponent);
	}

	/**
	 * Creates the index mapping strategy for the dirty component.
	 * @param dirtyComponent the dirty component to index.
	 * @return the {@link IIndexMappingStrategy index mapping strategy} for the dirty component.
	 */
	protected IIndexMappingStrategy createMappingStrategyForDirtyComponent(final T dirtyComponent) {
		return createMappingStrategy(dirtyComponent);
	}
	
	/**Returns with {@code true} if the object argument is a {@link CodeSystem} instance. Otherwise {@code false}.*/
	protected boolean isCodeSystem(final EObject object) {
		return TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(object.eClass());
	}
	
	/**Returns with {@code true} if the object argument is a {@link CodeSystemVersion} instance. Otherwise {@code false}.*/
	protected boolean isCodeSystemVersion(final EObject object) {
		return TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(object.eClass());
	}
	
	/**Returns with the code system index mapping strategy.*/
	protected final IIndexMappingStrategy createMappingStrategy(final CodeSystem codeSystem) {
		return new CodeSystemIndexMappingStrategy(codeSystem);
	}
	
	/**Returns with the index mapping strategy for the {@link CodeSystemVersion} argument.*/
	protected final IIndexMappingStrategy createMappingStrategy(final CodeSystemVersion version) {
		return new CodeSystemVersionIndexMappingStrategy(version);
	}
	
	@SuppressWarnings({ "restriction", "unchecked" }) 
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component) {
		final CodeSystemVersion codeSystemVersion = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID()).getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta).setValue(new Date(commitChangeSet.getTimestamp()));
			dirtyComponents.add((T) codeSystemVersion);
		}		
	}
}
