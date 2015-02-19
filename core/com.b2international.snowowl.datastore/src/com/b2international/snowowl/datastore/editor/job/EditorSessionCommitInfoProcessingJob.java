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
package com.b2international.snowowl.datastore.editor.job;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoProcessingJob;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 2.9
 */
public abstract class EditorSessionCommitInfoProcessingJob extends CDOCommitInfoProcessingJob {

	private Set<Long> deletedObjectStorageKeys = Collections.emptySet();
	private final CDOEditingContext editingContext;
	
	public EditorSessionCommitInfoProcessingJob(CDOObject component, CDOEditingContext editingContext, CDOCommitInfo commitInfo) {
		super(component, Preconditions.checkNotNull(editingContext, "editingContext").getTransaction(), commitInfo);
		this.editingContext = editingContext;
	}

	public Set<Long> getDeletedObjectStorageKeys() {
		return deletedObjectStorageKeys;
	}
	
	protected abstract boolean check(final InternalCDORevisionDelta delta, final EClass eClass);

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (null == getCommitInfo()) {
			return Status.CANCEL_STATUS;
		}

		// can happen if an editor is opened and the underlying component has been deleted -> do nothing.
		if (!checkTransaction()) {
			return Status.CANCEL_STATUS;
		}

		try {
			final Set<CDOID> remotelyChangedIds = Sets.newHashSet(Lists.transform(getCommitInfo().getChangedObjects(), CDOIDUtils.getIdAndVersionToIdFunction()));
			if (remotelyChangedIds.contains(getComponent().cdoID())) {
				// edited component has changed, refresh editor
				setAction(CommitInfoAction.MERGE_WITH_REFRESH);
			}

			deletedObjectStorageKeys = collectDeletedObjectStorageKeys(getCommitInfo());

			// if the edited object is "contained" we have to check if the underlying component is its container
			for (final CDORevisionKey revisionKey : getCommitInfo().getChangedObjects()) {
				if (revisionKey instanceof InternalCDORevisionDelta) {
					final InternalCDORevisionDelta delta = (InternalCDORevisionDelta) revisionKey;
					final EClass eClass = delta.getEClass();
					// we check the class first and we do not have to load object if not necessary
					if (check(delta, eClass)) {
						setAction(CommitInfoAction.MERGE_WITH_REFRESH); //editor has to refreshed
						return Status.OK_STATUS;
					}
				}
			}
		} catch (final InvalidObjectException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	protected Optional<CDOObject> getContainer(InternalCDORevisionDelta delta) {
		final CDOTransaction transaction = getTransactionSafe();
		if (null != transaction) {
			if (!delta.getID().isTemporary()) { //persisted value set member
				final CDOObject cdoObject = transaction.getObject(delta.getID());
				final EObject container = cdoObject.eContainer();
				if (container instanceof CDOObject) {
					return Optional.of((CDOObject) container);
				}
			}
		}
		return Optional.absent();
	}

	public static Set<Long> collectDeletedObjectStorageKeys(CDOCommitInfo commitInfo) {
		if (commitInfo != null) {
			List<CDOIDAndVersion> detachedObjects = commitInfo.getDetachedObjects();
			return ImmutableSet.<Long> copyOf(Collections2.transform(detachedObjects, new Function<CDOIDAndVersion, Long>() {
				@Override
				public Long apply(CDOIDAndVersion input) {
					return CDOIDUtils.asLong(input.getID());
				}
			}));
		}
		return Collections.emptySet();
	}

	public CDOEditingContext getEditingContext() {
		return editingContext;
	}

}