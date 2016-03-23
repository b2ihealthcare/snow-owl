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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Abstract superclass for {@link ICDOConflictProcessor}s that only want to report a subset of possible application-level conflicts.
 */
public abstract class AbstractCDOConflictProcessor implements ICDOConflictProcessor {

	private static final Map<EClass, EAttribute> EMPTY_MAP = ImmutableMap.of();

	private final String repositoryUuid;
	private final Map<EClass, EAttribute> releasedAttributeMap;
	private final Set<CDOID> idsToUnlink = newHashSet();

	protected AbstractCDOConflictProcessor(final String repositoryUuid) {
		this(repositoryUuid, EMPTY_MAP);
	}

	protected AbstractCDOConflictProcessor(final String repositoryUuid, final Map<EClass, EAttribute> releasedAttributeMap) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");
		checkNotNull(releasedAttributeMap, "EClass to released attribute map may not be null.");

		this.repositoryUuid = repositoryUuid;
		this.releasedAttributeMap = releasedAttributeMap;
	}

	@Override
	public final String getRepositoryUuid() {
		return repositoryUuid;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case will allow the add by returning {@code newInSource} (a {@link CDORevision}).
	 */
	@Override
	public Object addedInSource(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {
		return sourceRevision;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case will check if {@link #addedInSource(CDORevision, Map)} would return a {@link Conflict} in the
	 * same case, and if so, adds the identifier to the set of objects to unlink later; the addition is allowed
	 * otherwise by returning {@code targetRevision} (a {@link CDORevision}).
	 */
	@Override
	public Object addedInTarget(final CDORevision targetRevision, final Map<CDOID, Object> sourceMap) {
		if (addedInSource(targetRevision, sourceMap) instanceof Conflict) {
			idsToUnlink.add(targetRevision.getID());
		}

		return targetRevision;
	}

	protected Set<CDOID> getDetachedIdsInTarget(final Map<CDOID, Object> targetMap) {
		return ImmutableSet.copyOf(Iterables.filter(targetMap.values(), CDOID.class));
	}

	protected Iterable<InternalCDORevision> getNewRevisionsInTarget(final Map<CDOID, Object> targetMap) {
		return Iterables.filter(targetMap.values(), InternalCDORevision.class);
	}

	@Override
	public void postProcess(final CDOTransaction transaction) throws ConflictException {
		for (final CDOID idToUnlink : idsToUnlink) {
			final CDOObject objectIfExists = CDOUtils.getObjectIfExists(transaction, idToUnlink);
			if (objectIfExists != null) {
				unlinkObject(objectIfExists);
			}
		}
	}

	protected void unlinkObject(final CDOObject object) {
		EcoreUtil.remove(object);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case implements the same behavior as {@link DefaultCDOMerger.PerFeature}.
	 */
	@Override
	public CDOFeatureDelta changedInSourceAndTargetSingleValued(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta) {
		
		if (targetFeatureDelta.isStructurallyEqual(sourceFeatureDelta)) {
			return targetFeatureDelta;
		}

		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case will check if the change did not involve releasing the component, and if so, reports a
	 * conflict; allows the removal otherwise.
	 */
	@Override
	public Object changedInTargetAndDetachedInSource(final CDORevisionDelta targetDelta) {

		final Conflict conflict = checkReleasedId(targetDelta);
		if (conflict != null) {
			return conflict;
		}

		return targetDelta.getID();
	}

	private Conflict checkReleasedId(final CDORevisionDelta revisionDelta) {

		final EClass eClass = revisionDelta.getEClass();

		if (releasedAttributeMap.containsKey(eClass) && isReleased(revisionDelta, releasedAttributeMap.get(eClass))) {
			return new ChangedInSourceAndDetachedInTargetConflict(revisionDelta);
		} else {
			return null;
		}
	}

	private boolean isReleased(final CDORevisionDelta revisionDelta, final EAttribute releasedAttribute) {
		final CDOFeatureDelta releasedFeatureDelta = revisionDelta.getFeatureDelta(releasedAttribute);

		if (!(releasedFeatureDelta instanceof CDOSetFeatureDelta)) {
			return false;
		} else {
			return (boolean) ((CDOSetFeatureDelta) releasedFeatureDelta).getValue();
		}
	}
}
