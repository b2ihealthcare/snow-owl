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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
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
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
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
	
	@Override
	public Object detachedInSource(CDOID id) {
		return id;
	}

	protected Set<CDOID> getDetachedIdsInTarget(final Map<CDOID, Object> targetMap) {
		return ImmutableSet.copyOf(Iterables.filter(targetMap.values(), CDOID.class));
	}

	protected Iterable<InternalCDORevision> getNewRevisionsInTarget(final Map<CDOID, Object> targetMap) {
		return Iterables.filter(targetMap.values(), InternalCDORevision.class);
	}

	@Override
	public final Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView, final Map<CDOID, Conflict> conflicts, final boolean invertConflicts) {
		ImmutableList.Builder<MergeConflict> results = ImmutableList.builder();
		
		for (Conflict conflict : conflicts.values()) {
			if (invertConflicts) {
				conflict = invert(conflict);
			}
			
			final MergeConflict convertedConflict;
			if (invertConflicts) {
				// In this case, the values on the conflict object are already reversed, so the views must be swapped as well
				convertedConflict = convert(conflict, targetView, sourceView);
			} else {
				convertedConflict = convert(conflict, sourceView, targetView);
			}
			
			results.add(convertedConflict);
		}
		
		return results.build();
	}
	
	protected Conflict invert(final Conflict conflict) {
		if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			final ChangedInSourceAndDetachedInTargetConflict oldConflict = (ChangedInSourceAndDetachedInTargetConflict) conflict;
			return new ChangedInTargetAndDetachedInSourceConflict(oldConflict.getSourceDelta());
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			final ChangedInTargetAndDetachedInSourceConflict oldConflict = (ChangedInTargetAndDetachedInSourceConflict) conflict;
			return new ChangedInSourceAndDetachedInTargetConflict(oldConflict.getTargetDelta());
		} else if (conflict instanceof ChangedInSourceAndTargetConflict) {
			final ChangedInSourceAndTargetConflict oldConflict = (ChangedInSourceAndTargetConflict) conflict;
			return new ChangedInSourceAndTargetConflict(oldConflict.getTargetDelta(), oldConflict.getSourceDelta());
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			final AddedInSourceAndDetachedInTargetConflict oldConflict = (AddedInSourceAndDetachedInTargetConflict) conflict;
			return new AddedInTargetAndDetachedInSourceConflict(oldConflict.getTargetId(), oldConflict.getSourceId(), oldConflict.getFeatureName());
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			final AddedInTargetAndDetachedInSourceConflict oldConflict = (AddedInTargetAndDetachedInSourceConflict) conflict;
			return new AddedInSourceAndDetachedInTargetConflict(oldConflict.getTargetId(), oldConflict.getSourceId(), oldConflict.getFeatureName());
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			AddedInSourceAndTargetConflict oldConflict = (AddedInSourceAndTargetConflict) conflict;
			return new AddedInSourceAndTargetConflict(oldConflict.getTargetId(), oldConflict.getSourceId(), oldConflict.getMessage());
		}
		return conflict;
	}

	
	protected MergeConflict convert(final Conflict conflict, final CDOView sourceView, final CDOView targetView) {
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			return convert((ChangedInSourceAndTargetConflict) conflict, sourceView, targetView);
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			return convert((ChangedInSourceAndDetachedInTargetConflict) conflict, sourceView, targetView);
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			return convert((ChangedInTargetAndDetachedInSourceConflict) conflict, sourceView, targetView);
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			return convert((AddedInSourceAndTargetConflict) conflict, sourceView, targetView);
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			return convert((AddedInSourceAndDetachedInTargetConflict) conflict, sourceView, targetView);
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			return convert((AddedInTargetAndDetachedInSourceConflict) conflict, sourceView, targetView);
		}
		throw new IllegalArgumentException("Unknown conflict type: " + conflict);
	}
	
	protected MergeConflict convert(final ChangedInSourceAndTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getTargetDelta().getID().toString())
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
	}
	
	protected MergeConflict convert(final ChangedInSourceAndDetachedInTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getSourceDelta().getID().toString())
				.type(ConflictType.DELETED_WHILE_CHANGED)
				.build();
	}
	
	protected MergeConflict convert(final ChangedInTargetAndDetachedInSourceConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getTargetDelta().getID().toString())
				.type(ConflictType.CHANGED_WHILE_DELETED)
				.build();
	}
	
	protected MergeConflict convert(final AddedInSourceAndTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getTargetId().toString())
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
	}
	
	protected MergeConflict convert(final AddedInSourceAndDetachedInTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getTargetId().toString())
				.type(ConflictType.CAUSES_MISSING_REFERENCE)
				.build();
	}
	
	protected MergeConflict convert(final AddedInTargetAndDetachedInSourceConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(conflict.getTargetId().toString())
				.type(ConflictType.HAS_MISSING_REFERENCE)
				.build();
	}	
	
	@Override
	public void preProcess(Map<CDOID, Object> sourceMap, Map<CDOID, Object> targetMap) {
	}
	
	@Override
	public void postProcess(final CDOTransaction transaction) throws ConflictException {
		
		for (final CDOID idToUnlink : idsToUnlink) {
			final CDOObject objectIfExists = CDOUtils.getObjectIfExists(transaction, idToUnlink);
			if (objectIfExists != null) {
				unlinkObject(objectIfExists);
			}
		}
		
		List<MergeConflict> conflicts = FluentIterable.from(conflictRuleProvider.getRules())
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
