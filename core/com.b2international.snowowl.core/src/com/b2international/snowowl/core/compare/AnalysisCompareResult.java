/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.domain.ListCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.MoreObjects;

/**
 * Represents a summary of component changes between two versions of a terminology resource.
 * 
 * @since 9.0
 */
public final class AnalysisCompareResult extends ListCollectionResource<AnalysisCompareResultItem> implements Serializable {

	private static final String COUNTER_TOTAL = "total";
	private static final String COUNTER_NEW_COMPONENTS = "newComponents";
	private static final String COUNTER_CHANGED_COMPONENTS = "changedComponents";
	private static final String COUNTER_DELETED_COMPONENTS = "deletedComponents";

	private static final long serialVersionUID = 1L;

	private final ResourceURIWithQuery fromUri;
	private final ResourceURIWithQuery toUri;

	private List<NamedCount> counters;

	/**
	 * Creates a new change summary instance.
	 * 
	 * @param fromUri - the resource URI representing the comparison baseline
	 * @param toUri - the resource URI representing the comparison target
	 */
	public AnalysisCompareResult(final ResourceURIWithQuery fromUri, final ResourceURIWithQuery toUri) {
		this(null, fromUri, toUri);
	}
	
	/**
	 * Creates a new change summary instance.
	 * 
	 * @param items - the change detail items of this comparison
	 * @param fromUri - the resource URI representing the comparison baseline
	 * @param toUri - the resource URI representing the comparison target
	 */
	@JsonCreator
	public AnalysisCompareResult(
			@JsonProperty("items") final List<AnalysisCompareResultItem> items, 
			@JsonProperty("fromUri") final ResourceURIWithQuery fromUri, 
			@JsonProperty("toUri") final ResourceURIWithQuery toUri) {
		super(items);
		this.fromUri = checkNotNull(fromUri, "Resource URI 'fromUri' may not be null.");
		this.toUri = checkNotNull(toUri, "Resource URI 'toUri' may not be null.");
	}

	/**
	 * @return the resource URI representing the comparison baseline
	 */
	public ResourceURIWithQuery getFromUri() {
		return fromUri;
	}

	/**
	 * @return the resource URI representing the comparison target
	 */
	public ResourceURIWithQuery getToUri() {
		return toUri;
	}

	public List<NamedCount> getCounters() {
		return counters;
	}
	
	@JsonSetter
	void setCounters(List<NamedCount> counters) {
		this.counters = counters;
	}
	
	@JsonIgnore
	public Integer getTotalChanges() {
		return getCounterValue(COUNTER_TOTAL);
	}
	
	/**
	 * @return the number of added primary components between the two points of reference
	 */
	@JsonIgnore
	public Integer getNewComponents() {
		return getCounterValue(COUNTER_NEW_COMPONENTS);
	}

	@JsonIgnore
	public void setNewComponents(final Integer newComponents) {
		setCounterValue(COUNTER_NEW_COMPONENTS, newComponents);
		setCounterValue(COUNTER_TOTAL, Optional.ofNullable(getCounterValue(COUNTER_TOTAL)).orElse(0) + newComponents);
	}

	/**
	 * @return the number of changed primary components between the two points of reference
	 */
	@JsonIgnore
	public Integer getChangedComponents() {
		return getCounterValue(COUNTER_CHANGED_COMPONENTS);
	}

	@JsonIgnore
	public void setChangedComponents(final Integer changedComponents) {
		setCounterValue(COUNTER_CHANGED_COMPONENTS, changedComponents);
		setCounterValue(COUNTER_TOTAL, Optional.ofNullable(getCounterValue(COUNTER_TOTAL)).orElse(0) + changedComponents);
	}

	/**
	 * @return the number of removed primary components between the two points of reference
	 */
	@JsonIgnore
	public Integer getDeletedComponents() {
		return getCounterValue(COUNTER_DELETED_COMPONENTS);
	}

	@JsonIgnore
	public void setDeletedComponents(final Integer deletedComponents) {
		setCounterValue(COUNTER_DELETED_COMPONENTS, deletedComponents);
		setCounterValue(COUNTER_TOTAL, Optional.ofNullable(getCounterValue(COUNTER_TOTAL)).orElse(0) + deletedComponents);
	}

	public Integer getCounterValue(String counterName) {
		return this.counters == null ? null : this.counters.stream().filter(nc -> counterName.equals(nc.name())).findFirst().map(NamedCount::count).orElse(null);
	}
	
	public void setCounterValue(String counterName, int counterValue) {
		if (this.counters == null) {
			this.counters = new ArrayList<>(3);
		} else {
			// look for an existing counter and remove it
			this.counters.removeIf(nc -> nc.name().equals(counterName));
		}
		this.counters.add(new NamedCount(counterName, counterValue));
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("fromUri", fromUri)
			.add("toUri", toUri)
			.add("counters", counters)
			.add("items", getItems())
			.toString();
	}

}
