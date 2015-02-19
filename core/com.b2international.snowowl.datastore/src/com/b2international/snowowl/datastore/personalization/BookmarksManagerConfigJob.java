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
package com.b2international.snowowl.datastore.personalization;

import java.util.Collection;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.core.resources.IMarkerDelta;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.markers.ComponentMarker;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.core.markers.MarkerManager.MarkerChangeListener;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.serviceconfig.AbstractClientServiceConfigJob;

/**
 */
public class BookmarksManagerConfigJob extends AbstractClientServiceConfigJob<IBookmarksManager> {

	private enum BookmarksListener implements MarkerChangeListener {
		INSTANCE;
		
		private static IBookmarksManager getBookmarksManager() {
			return ApplicationContext.getInstance().getService(IBookmarksManager.class);
		}
		
		@Override
		public void handleMarkersAdded(final Collection<IMarkerDelta> markerDelta) {
			// Nothing to do; new markers don't have readable properties
		}

		@Override
		public void handleMarkersRemoved(final Collection<IMarkerDelta> markerDeltas) {
			for (IMarkerDelta markerDelta : markerDeltas) {
				final String terminologyComponentId = MarkerManager.getAttributeSafe(markerDelta, ComponentMarker.TERMINOLOGY_COMPONENT_ID, String.class);
				final String componentId = MarkerManager.getAttributeSafe(markerDelta, ComponentMarker.ID, String.class);
				if (terminologyComponentId != null && componentId != null) {
					getBookmarksManager().unregisterComponent(ComponentIdentifierPair.create(terminologyComponentId, componentId), getCurrentUserId());
				}
			}
		}

		@Override
		public void handleMarkersChanged(final Collection<IMarkerDelta> markerDeltas) {
			for (IMarkerDelta markerDelta : markerDeltas) {
				final String terminologyComponentId = MarkerManager.getAttributeSafe(markerDelta.getMarker(), ComponentMarker.TERMINOLOGY_COMPONENT_ID, String.class);
				final String componentId = MarkerManager.getAttributeSafe(markerDelta.getMarker(), ComponentMarker.ID, String.class);
				if (terminologyComponentId != null && componentId != null) {
					// XXX (apeteri): This may register components more than once
					getBookmarksManager().registerComponent(ComponentIdentifierPair.create(terminologyComponentId, componentId), getCurrentUserId());
				}
			}
		}
	}

	private static final String JOB_NAME = "Bookmarks service configuration...";

	private static String getCurrentUserId() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId();
	}

	public BookmarksManagerConfigJob() {
		super(JOB_NAME, DatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IBookmarksManager> getServiceClass() {
		return IBookmarksManager.class;
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected boolean initService() throws SnowowlServiceException {

		super.initService();
		
		// TODO: synchronize bookmarks with server on startup/service registration
		
		ApplicationContext.getInstance().addServiceListener(MarkerManager.class, new IServiceChangeListener<MarkerManager>() {
			@Override public void serviceChanged(MarkerManager oldService, MarkerManager newService) {
				if (null != oldService) {
					oldService.removeBookmarkMarkerChangeListener(BookmarksListener.INSTANCE);
				}
				
				if (null != newService) {
					newService.addBookmarkMarkerChangeListener(BookmarksListener.INSTANCE);
				}
			}
		});
		
		return true;
	}
}