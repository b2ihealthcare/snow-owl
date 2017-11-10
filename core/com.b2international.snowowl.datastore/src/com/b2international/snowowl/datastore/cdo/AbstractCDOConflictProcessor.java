/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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
	
	private final IMergeConflictRuleProvider conflictRuleProvider;

	protected AbstractCDOConflictProcessor(final String repositoryUuid) {
		this(repositoryUuid, EMPTY_MAP);
	}

	protected AbstractCDOConflictProcessor(final String repositoryUuid, final Map<EClass, EAttribute> releasedAttributeMap) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");
		checkNotNull(releasedAttributeMap, "EClass to released attribute map may not be null.");

		this.repositoryUuid = repositoryUuid;
		this.releasedAttributeMap = releasedAttributeMap;
		
		conflictRuleProvider = initializeConflictRuleProvider(repositoryUuid);
	}
	
	@Override
	public final String getRepositoryUuid() {
		return repositoryUuid;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case will allow the add by returning {@code sourceRevision} (a {@link CDORevision}).
	 */
	@Override
	public Object addedInSource(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {
		return sourceRevision;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default case will allow the add by returning {@code targetRevision} (a {@link CDORevision}).
	 */
	@Override
	public Object addedInTarget(final CDORevision targetRevision, final Map<CDOID, Object> sourceMap) {
		return targetRevision;
	}
	
	@Override
	public Object detachedInSource(CDOID id) {
		return id;
	}
	
	@Override
	public Object detachedInTarget(CDOID id) {
		return id;
	}

	protected Set<CDOID> getDetachedIds(final Map<CDOID, Object> revisionMap) {
		return ImmutableSet.copyOf(Iterables.filter(revisionMap.values(), CDOID.class));
	}

	protected Set<InternalCDORevision> getNewRevisions(final Map<CDOID, Object> revisionMap) {
		return ImmutableSet.copyOf(Iterables.filter(revisionMap.values(), InternalCDORevision.class));
	}

	@Override
	public Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView, final Map<CDOID, Conflict> conflicts) {
		if (!conflicts.isEmpty()) {
			return FluentIterable.from(conflicts.values()).transform(new Function<Conflict, MergeConflict>() {
				@Override public MergeConflict apply(Conflict input) {
					return ConflictMapper.convert(input);
				}
			}).toList();
		}
		return Collections.emptySet();
	}
	
	@Override
	public void preProcess(Map<CDOID, Object> sourceMap, Map<CDOID, Object> targetMap, CDOBranch sourceBranch, CDOBranch targetBranch, boolean isRebase) {
	}
	
	@Override
	public void postProcess(final CDOTransaction transaction) throws ConflictException {
		
		List<MergeConflict> conflicts = FluentIterable.from(getConflictRules())
				.transformAndConcat(new Function<IMergeConflictRule, Collection<MergeConflict>>() {
					@Override
					public Collection<MergeConflict> apply(IMergeConflictRule input) {
						return input.validate(transaction);
					}
				}).toList();

		if (!conflicts.isEmpty()) {
			throw new MergeConflictException(conflicts, "Domain specific conflicts detected while post-processing merge changes.");
		}
	}

	@Override
	public Collection<IMergeConflictRule> getConflictRules() {
		return conflictRuleProvider.getRules();
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

		final Conflict conflict = checkIsReleasedState(targetDelta);
		
		if (conflict != null) {
			return conflict;
		}

		return targetDelta.getID();
	}

	private Conflict checkIsReleasedState(final CDORevisionDelta revisionDelta) {

		final EClass eClass = revisionDelta.getEClass();

		Optional<EClass> releasableClass = FluentIterable.from(releasedAttributeMap.keySet()).firstMatch(new Predicate<EClass>() {
			@Override public boolean apply(EClass input) {
				return input.isSuperTypeOf(eClass);
			}
		});
		
		if (releasableClass.isPresent() && isReleased(revisionDelta, releasedAttributeMap.get(releasableClass.get()))) {
			return new ChangedInTargetAndDetachedInSourceConflict(revisionDelta);
		}
		
		return null;
	}

	private boolean isReleased(final CDORevisionDelta revisionDelta, final EAttribute releasedAttribute) {

		final CDOFeatureDelta releasedFeatureDelta = revisionDelta.getFeatureDelta(releasedAttribute);
		
		if (releasedFeatureDelta instanceof CDOSetFeatureDelta) {
			return (boolean) ((CDOSetFeatureDelta) releasedFeatureDelta).getValue();
		}
		
		return false;
	}

	private IMergeConflictRuleProvider initializeConflictRuleProvider(final String repositoryUuid) {
		
		Collection<IMergeConflictRuleProvider> ruleProviders = Extensions.getExtensions(IMergeConflictRuleProvider.EXTENSION_ID, IMergeConflictRuleProvider.class);
		Optional<IMergeConflictRuleProvider> ruleProvider = FluentIterable.from(ruleProviders).firstMatch(new Predicate<IMergeConflictRuleProvider>() {
			@Override
			public boolean apply(IMergeConflictRuleProvider ruleProvider) {
				return ruleProvider.getRepositoryUUID().equals(repositoryUuid);
			}
		});
		
		return ruleProvider.isPresent() ? ruleProvider.get() : new IMergeConflictRuleProvider.NullImpl(repositoryUuid);
	}
}
