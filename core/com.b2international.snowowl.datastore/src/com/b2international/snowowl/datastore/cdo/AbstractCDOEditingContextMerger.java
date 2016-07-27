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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.asLong;
import static com.google.common.collect.Iterables.get;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.exception.MergeFailedException;
import com.b2international.snowowl.datastore.exception.MergeFailedWithCDOIDException;
import com.b2international.snowowl.datastore.exception.MergeFailedWithDetailsException;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.HistoryInfoConfigurationImpl;
import com.b2international.snowowl.datastore.history.HistoryService;

/**
 * TODO add javadoc
 */
public abstract class AbstractCDOEditingContextMerger<E extends CDOEditingContext> implements ICDOEditingContextMerger<E> {

	/**
	 * Private logger instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCDOEditingContextMerger.class);
	
	/*returns with the change set data associated with the specified editing context*/
	protected CDOChangeSetData getChangeSetData(final E editingContext) {
		return editingContext.getTransaction().getChangeSetData();
	}

	/**
	 * Visits all the references of the specified eobject.
	 * @param eObject the object to visit the references.
	 * @param transaction an audit CDO view instance.
	 */
	protected void visitReferences(final EObject eObject, final CDOTransaction transaction) {
		for (final EStructuralFeature structuralFeature : eObject.eClass().getEAllReferences()) {
			if (structuralFeature.isMany()) {
				final Object object = eObject.eGet(structuralFeature);
				if (object instanceof Iterable) {
					for (final Object nextIterabel : (Iterable<?>) object) {
						if (nextIterabel instanceof EObject) {
							visitReferences((EObject) nextIterabel, transaction);
						}
					}
				} else {
					LOGGER.warn("Structural feature " + structuralFeature + " was many but not an instance of iterable for object " + object);
				}
			} else {
				final Object object = eObject.eGet(structuralFeature);
				if (object instanceof CDOObject) {
					reloadCDOObject((CDOObject) object, transaction);
				}
			}
		}
	}

	/**
	 * Reloads the specified object in the given CDO transaction.
	 * <br>This method does nothing if either the object is {@code null} or its ID is {@link CDOID#isTemporary() temporary} but logs a warning.
	 * @param object the object to reload.
	 * @param transaction the target CDO transaction where the object should be reloaded.
	 */
	protected void reloadCDOObject(final CDOObject object, final CDOTransaction transaction) {
		if (null == object) {
			LOGGER.warn("Specified object argument to reload was null.");
			return;
		}

		if (object.cdoID().isTemporary()) {
			//can be the new concept. e.g.: concept > outboundrelationship > source > concept
			return;
		}

		//reload the CDO object
		transaction.reload(object);
	}
	
	/*returns with the CDO object from the specified transaction based on the unique CDO ID*/
	protected CDOObject getObjectFromTransaction(final CDOID cdoId, final E context) {
		
		if (cdoId.isTemporary()) {
			final Map<CDOID, CDOObject> newObjects = context.getTransaction().getNewObjects();
			final CDOObject newObjectWithId = newObjects.get(cdoId);
			
			if (null == newObjectWithId) {
				throw new IllegalArgumentException(MessageFormat.format("Temporary CDOID {0} not found in new object map of the transaction.", cdoId));
			}

			return newObjectWithId;
		}
		
		return (CDOObject) context.lookup(CDOIDUtil.getLong(cdoId));
	}
	
	/*returns with the CDO object from the specified transaction based on the unique CDO ID*/
	protected CDOObject getObjectFromTransactionIfExists(final CDOID cdoId, final E context) {
		
		if (cdoId.isTemporary()) {
			final Map<CDOID, CDOObject> newObjects = context.getTransaction().getNewObjects();
			final CDOObject newObjectWithId = newObjects.get(cdoId);
			
			if (null == newObjectWithId) {
				throw new IllegalArgumentException(MessageFormat.format("Temporary CDOID {0} not found in new object map of the transaction.", cdoId));
			}
			
			return newObjectWithId;
		}
		
		return (CDOObject) context.lookupIfExists(CDOIDUtil.getLong(cdoId));
	}
	
	/*returns true if the specified object's direct resource is a CDO resource instance*/
	protected boolean hasDirectCDOResource(final CDOObject object) {
		return object.cdoDirectResource() instanceof CDOResource;
	}
	
	protected void handleObjectNotFoundException(final E newEditingContext, final ObjectNotFoundException e) 
			throws MergeFailedException, MergeFailedWithDetailsException {
		
		final CDOID cdoId = e.getID();

		//try extracting historical information
		final HistoryInfoConfiguration configuration = HistoryInfoConfigurationImpl.create(BranchPathUtils.createPath(newEditingContext.getBranch()), asLong(cdoId));
		final Collection<IHistoryInfo> historyInfo = getServiceForClass(HistoryService.class).getHistory(configuration);

		if (CompareUtils.isEmpty(historyInfo)) {
			throw new MergeFailedWithCDOIDException(cdoId);
		}

		final IHistoryInfo info = get(historyInfo, 0);
		final String conflictingAuthor = info.getAuthor();
		throw new MergeFailedWithDetailsException(conflictingAuthor, "Detached component: " + cdoId, cdoId);
	}

}