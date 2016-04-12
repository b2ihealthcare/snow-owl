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
package com.b2international.snowowl.core.markers;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.CoreTerminologyBroker.ICoreTerminologyComponentInformation;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Manages bookmark and problems markers.
 * 
 */
public class MarkerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MarkerManager.class);
	// mczotter, copied from: org.eclipse.ui.views.markers.MarkerViewUtil.NAME_ATTRIBUTE.
	public static final String MARKER_NAME = "org.eclipse.ui.views.markers.name";
	private final Map<MarkerChangeListener, IResourceChangeListener> listenerMap = Maps.newHashMap();
	
	/**
	 * Adds the specified listener, if it's not already added.
	 * @param listener
	 */
	public void addProblemMarkerChangeListener(final MarkerChangeListener listener) {
		addMarkerChangeListener(listener, IMarker.PROBLEM);
	}
	
	/**
	 * Adds the specified listener, if it's not already added.
	 * @param listener the listener to add
	 */
	public void addBookmarkMarkerChangeListener(final MarkerChangeListener listener) {
		addMarkerChangeListener(listener, IMarker.BOOKMARK);
	}
	
	private void addMarkerChangeListener(final MarkerChangeListener listener, final String type) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final MarkerResourceChangeListener markerResourceChangeListener = new MarkerResourceChangeListener(new MarkerChangeVisitor(listener, type));
		workspace.addResourceChangeListener(markerResourceChangeListener);
		listenerMap.put(listener, markerResourceChangeListener);
	}
	
	/**
	 * Removes the specified listener; does nothing if the listener was not added previously.
	 * @param listener
	 */
	public void removeProblemMarkerChangeListener(final MarkerChangeListener listener) {
		removeMarkerChangeListener(listener);
	}
	
	/**
	 * Removes the specified listener; does nothing if the listener was not added previously.
	 * @param listener the listener to remove
	 */
	public void removeBookmarkMarkerChangeListener(final MarkerChangeListener listener) {
		removeMarkerChangeListener(listener);
	}
	
	private void removeMarkerChangeListener(final MarkerChangeListener listener) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IResourceChangeListener resourceChangeListener = listenerMap.get(listener);
		if (null == resourceChangeListener) {
			return;
		}
		workspace.removeResourceChangeListener(resourceChangeListener);
		listenerMap.remove(listener);
	}
	
	public void bookmarkComponent(final IIndexEntry entry, final Object message) {
		bookmarkComponent(entry, message, entry.getStorageKey());
	}
	
	/**
	 * Bookmarks a terminology independent component by creating a new {@link IMarker marker} with {@link IMarker#BOOKMARK bookmark} 
	 * type and setting all other attribute of the marker related to the passed in <b>object</b>.<br><br>
	 * @param object the component to bookmark. Should not be {@code null}.
	 * @param message some human readable description of the bookmark. Can be {@code null}.
	 */
	public void bookmarkComponent(final Object object, final Object message, final long storageKey) {
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		CoreTerminologyBroker broker = CoreTerminologyBroker.getInstance();
		final IComponent<?> component = broker.adapt(object);
		Preconditions.checkNotNull(component, "Object argument cannot be adapted to any registered terminology component.");
		final String terminologyComponentId = broker.getTerminologyComponentId(object);
		final String id = String.valueOf(component.getId());
		try {
			
			final IMarker marker = getResource().createMarker(IMarker.BOOKMARK);
			marker.setAttribute(IMarker.MESSAGE, null == message ? "" : String.valueOf(message));
			marker.setAttribute(ComponentMarker.STORAGE_KEY, String.valueOf(storageKey));
			marker.setAttribute(ComponentMarker.ID, id);
			marker.setAttribute(ComponentMarker.TERMINOLOGY_COMPONENT_ID, terminologyComponentId);
			marker.setAttribute(ComponentMarker.LABEL, component.getLabel());
		} catch (final Exception e) {
			String componentInfo = "";
			final ICoreTerminologyComponentInformation information = broker.getComponentInformation(terminologyComponentId);
			if (null != information) {
				if (!StringUtils.isEmpty(information.getName())) {
					componentInfo = "Type: " + information.getName() + ", ";
				}
			}
			LOGGER.warn("Error while bookmarking component: " + component.getLabel() + ". [" + componentInfo + "ID: " + id + "]", e);
		}
	}

	/**
	 * Returns {@code true} if the passed in terminology independent component is bookmarked otherwise it returns with {@code false}. 
	 * If no TerminologyComponentId could be found, and an exception is thrown, then exception is caught and false is returned.
	 * @param object the terminology independent component.
	 * @return {@code true} if the component is bookmarked, {@code false} if not.
	 */
	public boolean isBookmarked(final Object object) {
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		final IComponent<?> component = CoreTerminologyBroker.getInstance().adapt(object);
		if (null == component)
			return false;
		try {
			final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(object);
			return isBookmarked(terminologyComponentId, String.valueOf(component.getId()));
		} catch (final Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns {@code true} if the passed in component is bookmarked otherwise it returns with {@code false}.
	 *  
	 * @param terminologyComponentId the terminology component type ID
	 * @param id the component ID
	 * @return {@code true} if the component is bookmarked, {@code false} if not.
	 */
	public boolean isBookmarked(final String terminologyComponentId, final String id) {
		final MarkerAttributeIdValuePair<String> componentIdPair = new MarkerAttributeIdValuePair<String>(
				ComponentMarker.ID, id);
		final MarkerAttributeIdValuePair<String> terminologyComponentIdPair = new MarkerAttributeIdValuePair<String>(
				ComponentMarker.TERMINOLOGY_COMPONENT_ID, terminologyComponentId);
		return !getMarkers(IMarker.BOOKMARK, componentIdPair, terminologyComponentIdPair).isEmpty();
	}
	
	
	/**
	 * Retrieves an attribute of a {@link IMarker marker} instance based on the passed in attribute name. The return result will be 
	 * casted to the required type based on the input <b>clazz</b> parameter.<br><br>
	 * <b>Note: </b>This method does not throw exception when getting the attribute or casting attribute to the required type 
	 * rather returns with {@code null}. 
	 * @param marker the marker instance.
	 * @param attributeName the name of the attribute.
	 * @param clazz the class of required return type.
	 * @return the attribute persisted to the resource instance.
	 * @param <T> type of the return value. 
	 */
	public static final <T> T getAttributeSafe(final IMarker marker, final String attributeName, final Class<T> clazz) {
		if (null == marker || null == attributeName ||  null == clazz) {
			return null;
		}
		
		try {
			final Object attribute = marker.getAttribute(attributeName);
			if (attribute.getClass().isAssignableFrom(clazz)) {
				return clazz.cast(attribute);
			}
		} catch (final Exception e) { /* ignore */ }
		
		return null;
	}
	
	/**
	 * Retrieves an attribute of a {@link IMarkerDelta marker delta} instance based on the passed in attribute name. The return result will be 
	 * casted to the required type based on the input <b>clazz</b> parameter.<br><br>
	 * <b>Note: </b>This method does not throw exception when getting the attribute or casting attribute to the required type 
	 * rather returns with {@code null}. 
	 * @param marker the marker instance.
	 * @param attributeName the name of the attribute.
	 * @param clazz the class of required return type.
	 * @return the attribute persisted to the resource instance.
	 * @param <T> type of the return value. 
	 */
	public static final <T> T getAttributeSafe(final IMarkerDelta markerDelta, final String attributeName, final Class<T> clazz) {
		if (null == markerDelta || null == attributeName ||  null == clazz) {
			return null;
		}

		try {
			final Object attribute = markerDelta.getAttribute(attributeName);
			if (attribute.getClass().isAssignableFrom(clazz)) {
				return clazz.cast(attribute);
			}
		} catch (final Exception e) { /* ignore */ }
		
		return null;
	}
	
	/**
	 * Returns with all persisted marker identified by the passed in marker type where the examined attribute value of the marker equals
	 * with the passed in attribute value.<br><br>
	 * <b>Note: </b> this method does not throws exception when working on persisted markers rather returns with an empty collection.
	 * @param markerType the type of the marker.(See: {@link IMarker#getType()})
	 * @param idValuePairs the pair of the examined attribute id and value.
	 * @return a collection of matching persisted markers.
	 */
	public final <T> Collection<IMarker> getMarkers(final String markerType, final MarkerAttributeIdValuePair<T>... idValuePairs) {
		return getMarkers(markerType, Arrays.asList(idValuePairs));
	}
	
	public final <T> Collection<IMarker> getMarkers(final String markerType, final List<MarkerAttributeIdValuePair<T>> idValuePairs) {
		Preconditions.checkNotNull(markerType, "Marker type argument cannot be null.");
		
		try {
			return Lists.newArrayList(Iterators.filter(Iterators.forArray(getResource().findMarkers(markerType, true, 
					IResource.DEPTH_INFINITE)), new Predicate<IMarker>() {
				@Override
				public boolean apply(final IMarker marker) {
					for (final MarkerAttributeIdValuePair<T> pair : idValuePairs) {
						final T markerValue = getAttributeSafe(marker, pair.getAttributeId(), pair.getType());
						if (null == markerValue || !markerValue.equals(pair.getAttributeValue())) {
							return false;
						}
					}
					return true;
				}
			}));
		} catch (final CoreException e) {
			LOGGER.warn("Error while retrieving markers from workspace. Marker type:" + markerType);
			return emptyList();
		}
	}
	
	/**
	 * Deletes a persisted marker. This method does nothing if the passed in marker is not a persisted one.
	 * @param marker the marker to delete.
	 */
	public void deleteMarker(final IMarker... markers) {
		for (final IMarker marker : markers) {
			try {
				marker.delete();
			} catch (final CoreException e) {
				LOGGER.warn("Error while deleting marker: " + marker);
				//does nothing if the marker is not a persisted one
			}
		}
	}
	
	/**
	 * Deletes all persisted markers associated with the specified terminology component represented as a {@link ComponentIdentifierPair component identifier pairs}.
	 * @param identifierPairs terminology independent components represented as {@link ComponentIdentifierPair}s.
	 */
	public <K> void deleteMarkersForComponent(final Iterable<ComponentIdentifierPair<K>> identifierPairs) {
		final Set<IMarker> markers = Sets.newHashSet();
		for (final ComponentIdentifierPair<K> identifierPair : identifierPairs) {
			markers.addAll(getProblemMarkersForComponent(identifierPair));
			markers.addAll(getBookmarkMarkersForComponent(identifierPair));
		}
		deleteMarker(Iterables.toArray(markers, IMarker.class));
	}
	
	/**
	 * Deletes all persisted markers associated with the specified terminology component represented as a {@link ComponentIdentifierPair component identifier pairs}.
	 * @param identifierPairs terminology independent components represented as {@link ComponentIdentifierPair}s.
	 */
	public <K> void deleteMarkersForComponent(final ComponentIdentifierPair<K>... identifierPairs) {
		deleteMarkersForComponent(Sets.newHashSet(identifierPairs));
	}

	/**
	 * Creates a problem marker for a terminology independent component by creating a new {@link IMarker marker} with 
	 * {@link IMarker#PROBLEM problem} type and setting all other attribute of the marker related to the passed 
	 * in <b>object</b> and the <b>diagnostic</b>. Updates and removes persisted markers if necessary.<br><br>
	 * @param object the component to mark with a problem. Should not be {@code null}.
	 * @param diagnostic information about the outcome of the component validation process.
	 */
	public void createValidationMarkerOnComponent(final Object object, final IDiagnostic diagnostic) {
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		Preconditions.checkNotNull(diagnostic, "Diagnostic argument cannot be null.");
		Preconditions.checkNotNull(diagnostic.getChildren(), "The details (children diagnostics) of diagnostic argument cannot be null.");
		final IComponent<?> component = CoreTerminologyBroker.getInstance().adapt(object);
		Preconditions.checkNotNull(component, "Object argument cannot be adapted to any registered terminology component.");
		Preconditions.checkNotNull(component.getId(), "The unique identifier of the component cannot be null.");
		final String id = String.valueOf(component.getId());
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(object);
		Preconditions.checkNotNull(terminologyComponentId, "The terminology component identifier of the component cannot be null.");
		
		try {
			final Set<IMarker> markersWithSameComponent = getExistingProblemMarkers(id, terminologyComponentId);

			if (!diagnostic.getChildren().isEmpty()) {
				// if it has children, then create markers from those
				for (final IDiagnostic childDiagnostic : diagnostic.getChildren()) {
					createProblemMarker(component, terminologyComponentId, childDiagnostic, markersWithSameComponent);
				}
			} else {
				// if it has no children, create marker from it
				createProblemMarker(component, terminologyComponentId, diagnostic, markersWithSameComponent);
			}
			
		} catch (final Exception e) {
			String componentInfo = "";
			final ICoreTerminologyComponentInformation information = CoreTerminologyBroker.getInstance().getComponentInformation(terminologyComponentId);
			if (null != information) {
				if (!StringUtils.isEmpty(information.getName())) {
					componentInfo = "Type: " + information.getName() + ", ";
				}
			}
			LOGGER.warn("Error while creating problem marker for component: " + component.getLabel() + ". [" + componentInfo + "ID: " + id + "]", e);
		}
		
	}

	private Set<IMarker> getExistingProblemMarkers(final String id, final String terminologyComponentId) {
		try {
			return FluentIterable.from(Arrays.asList(getResource().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE))).filter(new Predicate<IMarker>() {
				@Override public boolean apply(IMarker marker) {
					final String componentIdAttributeValue = getAttributeSafe(marker, ComponentMarker.ID, String.class);
					final String componentTerminologyComponentIdAttributeValue = getAttributeSafe(marker, ComponentMarker.TERMINOLOGY_COMPONENT_ID, String.class);
					return id.equals(componentIdAttributeValue)	&& terminologyComponentId.equals(componentTerminologyComponentIdAttributeValue);
				}
			}).toSet();
		} catch (Exception e) {
			LOGGER.warn("Retrieving existing component markers failed:", e);
			return emptySet();
		}
	}
	
	/* Creates a problem marker and persists it into the workspace root. If there was a problem marker on the same component with the same source (e.g. constraint ID), 
	 * the old problem marker is deleted first. */
	private void createProblemMarker(final IComponent<?> component, final String terminologyComponentId, final IDiagnostic diagnostic, final Set<IMarker> markersWithSameComponent) throws CoreException {
		// only create markers if this is a leaf diagnostic
		if (diagnostic.getChildren().isEmpty()) {
			// delete markers with same source (e.g. constraint ID)
			for (final IMarker marker : markersWithSameComponent) {
				final String constraintIdAttributeValue = getAttributeSafe(marker, ComponentMarker.CONSTRAINT_ID, String.class);
				if (constraintIdAttributeValue != null && constraintIdAttributeValue.equals(diagnostic.getSource())) {
					marker.delete();
				}
			}
			
			// don't create markers for OK diagnostic results
			if (diagnostic.getProblemMarkerSeverity().equals(DiagnosticSeverity.OK))
				return;
			
			final IMarker marker = getResource().createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, null == diagnostic.getMessage() ? "" : diagnostic.getMessage());
			marker.setAttribute(IMarker.SEVERITY, mapDiagnosticSeverity(diagnostic.getProblemMarkerSeverity()));
			// mczotter, if you set the marker's name attribute with the following specified name, 
			// the QuickFix wizard shows the attribute's value as resource name
			// Set the marker's location for the diagnostic message
			marker.setAttribute(MARKER_NAME, component.getLabel());
			marker.setAttribute(IMarker.LOCATION, diagnostic.getMessage());
			marker.setAttribute(ComponentMarker.ID, String.valueOf(component.getId()));
			marker.setAttribute(ComponentMarker.TERMINOLOGY_COMPONENT_ID, terminologyComponentId);
			marker.setAttribute(ComponentMarker.CONSTRAINT_ID, diagnostic.getSource());
			marker.setAttribute(ComponentMarker.LABEL, component.getLabel());
		} else {
			for (final IDiagnostic childDiagnostic : diagnostic.getChildren()) {
				createProblemMarker(component, terminologyComponentId, childDiagnostic, markersWithSameComponent);
			}
		}
	}
	
	private int mapDiagnosticSeverity(final DiagnosticSeverity severity) {
		switch (severity) {
		case ERROR:
			return IMarker.SEVERITY_ERROR;
		case WARNING:
			return IMarker.SEVERITY_WARNING;
		case INFO:
			return IMarker.SEVERITY_INFO;
		default:
			throw new IllegalArgumentException("Unexpected diagnostic severity: " + severity);
		}
	}
	
	/*Returns with the workspace root.*/
	private final IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	private <K> Collection<IMarker> getBookmarkMarkersForComponent(final ComponentIdentifierPair<K> identifierPair) {
		return getMarkers(IMarker.BOOKMARK, createForTerminologyComponentId(identifierPair), this.<K>createForComponentId(identifierPair));
	}

	private <K> Collection<IMarker> getProblemMarkersForComponent(final ComponentIdentifierPair<K> identifierPair) {
		return getMarkers(IMarker.PROBLEM, createForTerminologyComponentId(identifierPair), this.<K>createForComponentId(identifierPair));
	}

	private <K> MarkerAttributeIdValuePair<K> createForTerminologyComponentId(final ComponentIdentifierPair<K> pair) {
		return new MarkerAttributeIdValuePair<K>(ComponentMarker.TERMINOLOGY_COMPONENT_ID, (K) pair.getTerminologyComponentId());
	}
	
	private <K> MarkerAttributeIdValuePair<K> createForComponentId(final ComponentIdentifierPair<K> pair) {
		return new MarkerAttributeIdValuePair<K>(ComponentMarker.ID, pair.getComponentId());
	}
	
	/**
	 * Simplified interface for receiving batched notifications of resource marker related events.
	 * 
	 */
	public interface MarkerChangeListener {
		
		/**
		 * Notifies this listener about added markers.
		 * 
		 * @param markerDeltas the added markers
		 */
		void handleMarkersAdded(Collection<IMarkerDelta> markerDeltas);
		
		/**
		 * Notifies this listener about removed markers.
		 * 
		 * @param markerDeltas
		 */
		void handleMarkersRemoved(Collection<IMarkerDelta> markerDeltas);
		
		/**
		 * Notifies this listener about changed markers.
		 * 
		 * @param markerDeltas
		 */
		void handleMarkersChanged(Collection<IMarkerDelta> markerDeltas);
	}
	
	/**
	 * Empty {@link MarkerChangeListener} implementation. Clients can subclass methods as they see fit.
	 *  
	 */
	public class MarkerChangeAdapter implements MarkerChangeListener {
		@Override
		public void handleMarkersAdded(final Collection<IMarkerDelta> markerDelta) {}

		@Override
		public void handleMarkersRemoved(final Collection<IMarkerDelta> markerDeltas) {}

		@Override
		public void handleMarkersChanged(final Collection<IMarkerDelta> markerDeltas) {}
	}
	
	/**
	 * Generic resource change listener implementation.
	 */
	private static final class MarkerResourceChangeListener implements IResourceChangeListener {
		private final IResourceDeltaVisitor visitor;
		
		public MarkerResourceChangeListener(final IResourceDeltaVisitor visitor) {
			this.visitor = visitor;
		}
		
		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			final IResourceDelta delta = event.getDelta();
			if (delta != null) {
				try {
					delta.accept(visitor);
				} catch (final CoreException ce) {
					
				}
			}
		}
	};
	
	/**
	 * Resource delta visitor implementation, which wraps a {@link MarkerChangeListener} instance
	 * and delegates marker related events to it.
	 */
	private static final class MarkerChangeVisitor implements IResourceDeltaVisitor {
		private final MarkerChangeListener delegate;
		private final String type;

		public MarkerChangeVisitor(final MarkerChangeListener delegate, final String type) {
			this.delegate = delegate;
			this.type = type;
		}

		@Override
		public boolean visit(final IResourceDelta delta) throws CoreException {
			if (delta == null)
				return false;
			final IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();
			final Builder<IMarkerDelta> addedDeltaBuilder = ImmutableList.builder();
			final Builder<IMarkerDelta> removedDeltaBuilder = ImmutableList.builder();
			final Builder<IMarkerDelta> changedDeltaBuilder = ImmutableList.builder();
			for (int i = 0; i < markerDeltas.length; i++) {
				final IMarkerDelta markerDelta = markerDeltas[i];
				if (markerDelta.isSubtypeOf(type)) {
					switch (markerDelta.getKind()) {
						case IResourceDelta.ADDED:
							addedDeltaBuilder.add(markerDelta);
							break;
						case IResourceDelta.REMOVED:
							removedDeltaBuilder.add(markerDelta);
							break;
						case IResourceDelta.CHANGED:
							changedDeltaBuilder.add(markerDelta);
							break;
					}
				}
			}
			delegate.handleMarkersAdded(addedDeltaBuilder.build());
			delegate.handleMarkersRemoved(removedDeltaBuilder.build());
			delegate.handleMarkersChanged(changedDeltaBuilder.build());
			return true;
		}
	}
}