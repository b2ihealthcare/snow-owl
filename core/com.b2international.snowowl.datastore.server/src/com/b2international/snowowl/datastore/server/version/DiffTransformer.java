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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.ChangeKind.DELETED;
import static com.b2international.commons.ChangeKind.UPDATED;
import static com.b2international.snowowl.datastore.index.diff.FeatureChange.EMPTY_INSTANCE;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.parseBoolean;
import static java.util.Collections.singleton;

import java.util.Collection;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.datastore.index.diff.FeatureChange;
import com.b2international.snowowl.datastore.index.diff.FeatureChangeImpl;
import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeChangeImpl;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.datastore.index.diff.NodeDeltaImpl;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.emf.compare.diff.Diff;

/**
 * Base representation of a difference transformer for the compare feature.
 *
 */
public abstract class DiffTransformer {

	/**Returns with {@code true} if the diff represents an addition.*/
	protected boolean isAddition(final Diff<?, ?> diff) {
		return checkNotNull(diff, "diff").getChange().isNew();
	}

	/**Returns with {@code true} if the diff represents a deletion.*/
	protected boolean isDeletion(final Diff<?, ?> diff) {
		return checkNotNull(diff, "diff").getChange().isDeleted();
	}
	
	/**Returns with {@code true} if the diff represents an addition.*/
	protected boolean isAddition(final NodeDiff diff) {
		return checkNotNull(diff, "diff").getChange().isNew();
	}

	/**Returns with {@code true} if the diff represents a deletion.*/
	protected boolean isDeletion(final NodeDiff diff) {
		return checkNotNull(diff, "diff").getChange().isDeleted();
	}
	
	/**Returns with {@code true} if the diff represents an update of an existing node.*/
	protected boolean isUpdate(final NodeDiff diff) {
		return checkNotNull(diff, "diff").getChange().isDirty();
	}
	
	/**Returns with the string {@code Yes} if the given argument is {@code true}. Otherwise it returns with {@code No}.*/
	protected String toYesNo(final Object value) {
		return parseBoolean(String.valueOf(value)) ? "Yes" : "No";
	}
	
	/**Returns with the string {@code Active} if the given argument is {@code true}. Otherwise it returns with {@code Inactive}.*/
	protected String toActiveInactive(final Object value) {
		return parseBoolean(String.valueOf(value)) ? "Active" : "Inactive";
	}
	
	/**Returns with the string {@code Published} if the given argument is {@code true}. Otherwise it returns with {@code Unreleased}.*/
	protected String toPublishedUnreleased(final Object value) {
		return parseBoolean(String.valueOf(value)) ? "Published" : "Unreleased";
	}
	
	/**Wraps the given argument into a single element collection and returns with it.*/
	protected Collection<NodeDelta> toCollection(final NodeDelta delta) {
		return singleton(delta);
	}
	
	/**Creates a node change from the underlying {@link NodeDiff node difference} and an iterable of {@link NodeDelta delta}s.*/
	protected NodeChange createNodeChange(final NodeDiff diff, final CDOView sourceView, 
			final CDOView targetView, final Iterable<? extends NodeDelta> deltas) {
		
		return new NodeChangeImpl(checkNotNull(diff).getId(), diff.getLabel(), deltas);
	}
	
	/**Returns with an empty feature change.*/
	protected FeatureChange createEmptyFeatureChange() {
		return EMPTY_INSTANCE;
	}
	
	/**Returns with a feature change containing only the name of the feature.*/
	protected FeatureChange createEmptyFeatureChange(final String featureName) {
		return FeatureChangeImpl.createFeatureChange(featureName, null, null);
	}
	
	/**Creates and returns with a new feature change instance.*/
	protected FeatureChange createFeatureChange(final String featureName, @Nullable final String fromValue, @Nullable final String toValue) {
		return FeatureChangeImpl.createFeatureChange(featureName, fromValue, toValue);
	}
	
	/**Creates and returns with a new FROM feature change instance.*/
	protected FeatureChange createFromFeatureChange(final String fromValue) {
		return FeatureChangeImpl.createFromFeatureChange(null, fromValue);
	}

	/**Creates and returns with a new FROM feature change instance.*/
	protected FeatureChange createFromFeatureChange(final String featureName, final String fromValue) {
		return FeatureChangeImpl.createFromFeatureChange(featureName, fromValue);
	}
	
	/**Creates and returns with a new TO feature change instance.*/
	protected FeatureChange createToFeatureChange(final String toValue) {
		return FeatureChangeImpl.createToFeatureChange(null, toValue);
	}
	
	/**Creates and returns with a new TO feature change instance.*/
	protected FeatureChange createToFeatureChange(final String featureName, final String toValue) {
		return FeatureChangeImpl.createToFeatureChange(featureName, toValue);
	}
	
	/**
	 * Creates a new node delta with the given {@link FeatureChange feature change}, component type and 
	 * {@link ChangeKind#ADDED} change kind.
	 */
	protected NodeDelta createDeltaForAddition(final String label, final FeatureChange featureChange, final short terminologyComponentId) {
		return createDelta(label, featureChange, terminologyComponentId, ADDED);
	}

	/**
	 * Creates a new node delta with the given {@link FeatureChange feature change}, component type and 
	 * {@link ChangeKind#UPDATED} change kind.
	 */
	protected NodeDelta createDeltaForUpdate(final String label, final FeatureChange featureChange, final short terminologyComponentId) {
		return createDelta(label, featureChange, terminologyComponentId, UPDATED);
	}
	
	/**
	 * Creates a new node delta with the given {@link FeatureChange feature change}, component type and 
	 * {@link ChangeKind#DELETED} change kind.
	 */
	protected NodeDelta createDeltaForDeletion(final String label, final FeatureChange featureChange, final short terminologyComponentId) {
		return createDelta(label, featureChange, terminologyComponentId, DELETED);
	}
	
	/**
	 * Creates a new node delta with the given {@link FeatureChange feature change}, component type and change kind.
	 */
	protected NodeDelta createDelta(final String label, final FeatureChange featureChange, final short terminologyComponentId, final Change change) {
		return new NodeDeltaImpl(
				checkNotNull(label, "label"), 
				checkNotNull(featureChange, "featureChange"), 
				terminologyComponentId, 
				checkNotNull(change, "change"));
	}
}