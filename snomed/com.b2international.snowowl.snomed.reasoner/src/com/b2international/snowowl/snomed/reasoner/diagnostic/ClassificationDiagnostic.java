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
package com.b2international.snowowl.snomed.reasoner.diagnostic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IMarker;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.markers.ComponentMarker;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.MarkerAttributeIdValuePair;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * {@link IDiagnostic} implementation that represents a diagnostic as an outcome of the classification process. 
 */
public class ClassificationDiagnostic implements IDiagnostic {

	/**Unique identifier of the source. (Value: {@value})*/
	public static final String SOURCE_ID = "com.b2international.snowowl.snomed.reasoner.model.ClassificationDiagnostic";
	
	private static class Child implements IDiagnostic {
		
		private final String message;

		private Child(final String message) {
			this.message = message;
		}

		@Override
		public boolean isOk() {
			return false;
		}

		@Override
		public DiagnosticSeverity getProblemMarkerSeverity() {
			return DiagnosticSeverity.ERROR;
		}

		@Override
		public Collection<IDiagnostic> getChildren() {
			return Collections.emptyList();
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getSource() {
			return SOURCE_ID;
		}
	}
	
	private final Child child;
	
	public ClassificationDiagnostic(final String message) {
		this.child = new Child(message);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.markers.IDiagnostic#isProblem()
	 */
	@Override
	public boolean isOk() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.markers.IDiagnostic#getProblemMarkerSeverity()
	 */
	@Override
	public DiagnosticSeverity getProblemMarkerSeverity() {
		return DiagnosticSeverity.ERROR;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.markers.IDiagnostic#getChildren()
	 */
	@Override
	public Collection<IDiagnostic> getChildren() {
		return Collections.<IDiagnostic>singletonList(child);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.markers.IDiagnostic#getMessage()
	 */
	@Override
	public String getMessage() {
		return child.getMessage();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.markers.IDiagnostic#getSource()
	 */
	@Override
	public String getSource() {
		return SOURCE_ID;
	}

	/**
	 * 
	 * @param equivalentConcepts
	 */
	public static void updateMarkers(final Iterable<AbstractEquivalenceSet> equivalentConcepts) {
		final MarkerManager markerManager = ApplicationContext.getInstance().getService(MarkerManager.class);
		
		@SuppressWarnings("unchecked")
		final Collection<IMarker> persistedMarkers = markerManager.getMarkers(IMarker.PROBLEM, 
				new MarkerAttributeIdValuePair<String>(ComponentMarker.CONSTRAINT_ID, ClassificationDiagnostic.SOURCE_ID));
		
		//wrap markers into a map where the key is the terminology component identifier
		//component identifier pair and the value is the marker instance
		final Map<ComponentIdentifierPair<String>, IMarker> markersMap = Maps.newHashMap(Maps.uniqueIndex(persistedMarkers, new Function<IMarker, ComponentIdentifierPair<String>>() {

			//implements com.google.common.base.Function<org.eclipse.core.resources
			//.IMarker,com.b2international.snowowl.core.ComponentIdentifierPair<java.lang.String>>.apply
			@Override
			public ComponentIdentifierPair<String> apply(final IMarker marker) {
				final String id = MarkerManager.getAttributeSafe(marker, ComponentMarker.ID, String.class);
				final String terminologyComponentId = MarkerManager.getAttributeSafe(marker, ComponentMarker.TERMINOLOGY_COMPONENT_ID, String.class);
				return ComponentIdentifierPair.<String>create(terminologyComponentId, id);
			}
		}));
		
		for (final AbstractEquivalenceSet set : equivalentConcepts) {
			for (final SnomedConceptIndexEntry conceptEntry: set.getConcepts()) {
				//if the problem already marked do delete it and create a new one
				final ComponentIdentifierPair<String> pair = ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.CONCEPT, conceptEntry.getId());
				if (markersMap.containsKey(pair)) {
					markerManager.deleteMarker(markersMap.get(pair));
					markerManager.createValidationMarkerOnComponent(conceptEntry, new ClassificationDiagnostic(set.getTitle()));
					markersMap.remove(pair);
				} else {
					markerManager.createValidationMarkerOnComponent(conceptEntry, new ClassificationDiagnostic(set.getTitle()));
				}
			}
		}
		
		// Remove the rest of the markers not handled previously
		final Collection<IMarker> remainingMarkers = markersMap.values();
		markerManager.deleteMarker((IMarker[]) remainingMarkers.toArray(new IMarker[remainingMarkers.size()]));
	}
}