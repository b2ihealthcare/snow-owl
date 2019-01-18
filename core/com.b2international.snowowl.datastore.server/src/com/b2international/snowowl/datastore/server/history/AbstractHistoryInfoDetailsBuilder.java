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
package com.b2international.snowowl.datastore.server.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.history.HistoryInfoDetailsBuilder;

/**
 *
 */
public abstract class AbstractHistoryInfoDetailsBuilder implements HistoryInfoDetailsBuilder {

	private List<ExtendedLocale> locales;
	
	@Override
	public void configureLocales(List<ExtendedLocale> locales) {
		this.locales = Collections3.toImmutableList(locales);
	}
	
	protected final List<ExtendedLocale> getLocales() {
		return locales;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.history.IHistoryInfoDetailsBuilder#buildDetails(org.eclipse.emf.cdo.view.CDOView, org.eclipse.emf.cdo.view.CDOView, org.eclipse.emf.cdo.common.commit.CDOCommitInfo)
	 */
	@Override
	public Collection<IHistoryInfoDetails> buildDetails(final CDOView currentView, final CDOView beforeView, final CDOCommitInfo commitInfo) {
		final List<IHistoryInfoDetails> details = new ArrayList<IHistoryInfoDetails>();
		
		details.addAll(processNewObjects(commitInfo.getNewObjects(), beforeView, currentView));
		details.addAll(processDetachedObjects(commitInfo.getDetachedObjects(), beforeView, currentView));
		details.addAll(processChangedObjects(commitInfo.getChangedObjects(), beforeView, currentView));
		
		return details;
	}
	
	protected Collection<? extends IHistoryInfoDetails> processNewObjects(final List<CDOIDAndVersion> newObjects, final CDOView beforeView, final CDOView currentView) {
		return processNewObjects(newObjects, beforeView, currentView, false);
	}
	
	protected Collection<? extends IHistoryInfoDetails> processNewObjects(final List<CDOIDAndVersion> newObjects, final CDOView beforeView, final CDOView currentView, final boolean shouldInvalidate) {
		final List<IHistoryInfoDetails> infoDetails = new ArrayList<IHistoryInfoDetails>();
		for (final CDOIDAndVersion idAndVersion : newObjects) {
			final CDOID cdoid = ((CDORevision) idAndVersion).getID();
			final CDOObject object = CDOUtils.getObjectIfExists(currentView, cdoid);
			if (null == object) { //object was created on the main branch we have to open a view on the main branch
				final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
				final ICDOConnection connection = connectionManager.get(currentView); 
				final CDOBranch mainBranch = connection.getMainBranch();
				final CDOView cdoMainView = connection.getSession().openView(mainBranch, currentView.getTimeStamp(), shouldInvalidate);
				try {
					IHistoryInfoDetails details = generateInfoForNewObject(cdoMainView.getObject(cdoid), beforeView, cdoMainView);
					if (IHistoryInfoDetails.IGNORED_DETAILS != details) {
						infoDetails.add(details);
					}
				} finally {
					cdoMainView.close();
				}
			} else {
				final IHistoryInfoDetails details = generateInfoForNewObject(object, beforeView, currentView);
				if (IHistoryInfoDetails.IGNORED_DETAILS != details) {
					infoDetails.add(details);
				}
			}
		}
		return infoDetails;
	}

	protected Collection<? extends IHistoryInfoDetails> processDetachedObjects(final List<CDOIDAndVersion> detachedObjects, final CDOView beforeView, final CDOView currentView) {
		final List<IHistoryInfoDetails> infoDetails = new ArrayList<IHistoryInfoDetails>();
		for (final CDOIDAndVersion idAndVersion : detachedObjects) {
			final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
			final ICDOConnection connection = connectionManager.get(idAndVersion.getID());
			final CDOView beforeCdoView = connection.getSession().openView(beforeView.getBranch(), beforeView.getTimeStamp(), false);
			try {
				final CDOID cdoid = idAndVersion.getID();
				final CDOObject object = CDOUtils.getObjectIfExists(beforeCdoView, cdoid);
				if (null == object) { //object has been deleted on main we should open a view on the main branch
					final CDOBranch mainBranch = connection.getMainBranch();
					final CDOView beforeMainCdoView = connection.getSession().openView(mainBranch, beforeView.getTimeStamp() - 1, false);
					try {
						final IHistoryInfoDetails details = generateInfoForDetachedObject(beforeMainCdoView.getObject(cdoid), beforeMainCdoView, currentView);
						if (IHistoryInfoDetails.IGNORED_DETAILS != details) {
							infoDetails.add(details);
						}
					} finally {
						beforeMainCdoView.close();
					}
				} else {
					final IHistoryInfoDetails details = generateInfoForDetachedObject(object, beforeCdoView, currentView);
					if (IHistoryInfoDetails.IGNORED_DETAILS != details) {
						infoDetails.add(details);
					}
				}
			} finally {
				beforeCdoView.close();
			}
		}
		return infoDetails;
	}

	protected Collection<? extends IHistoryInfoDetails> processChangedObjects(final List<CDORevisionKey> changedObjects, final CDOView beforeView, final CDOView currentView) {
		final List<IHistoryInfoDetails> infoDetails = new ArrayList<IHistoryInfoDetails>();
		for (final CDORevisionKey revisionKey : changedObjects) {
			if (revisionKey instanceof CDORevisionDelta) {
				final CDORevisionDelta revisionDelta = (CDORevisionDelta) revisionKey;
				for (final CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas()) {
					if (Type.SET.equals(featureDelta.getType()) && filter(featureDelta.getFeature())) { // TODO: change to instanceof CDOSetFeatureDelta?
						final CDOObject object = currentView.getObject(revisionKey.getID());
						final IHistoryInfoDetails details = generateInfoForChangedObject(object, currentView, beforeView, (CDOSetFeatureDelta) featureDelta);
						if (IHistoryInfoDetails.IGNORED_DETAILS != details) {
							infoDetails.add(details);
						}
					}
				}
			}
		}
		return infoDetails;
	}

	protected abstract IHistoryInfoDetails generateInfoForNewObject(CDOObject object, CDOView beforeView, CDOView currentView);

	protected abstract IHistoryInfoDetails generateInfoForDetachedObject(CDOObject object, CDOView beforeView, CDOView currentView);

	protected abstract IHistoryInfoDetails generateInfoForChangedObject(CDOObject object, CDOView currentView, CDOView beforeView, CDOSetFeatureDelta featureDelta);
	
	/**
	 * Filter method for filtering change deltas about
	 * {@link EStructuralFeature} instances.
	 * 
	 * @param feature
	 * @return <code>true</code> if the specified feature is allowed in the
	 *         details part of the History view, <code>false</code> otherwise.
	 */
	protected boolean filter(final EStructuralFeature feature) {
		return true;
	}

	public abstract String getDescription(final CDOObject cdoObject, CDOView beforeView, CDOView currentView, final String change, final String refsetChange);
}