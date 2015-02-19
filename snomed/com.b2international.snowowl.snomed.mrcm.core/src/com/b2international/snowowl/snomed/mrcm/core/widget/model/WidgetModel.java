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
package com.b2international.snowowl.snomed.mrcm.core.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Set;

import com.b2international.snowowl.datastore.utils.UnrestrictedStringSet;

/**
 * Represents an abstract model for a backing bean.
 * <p>
 * Subclasses form a simplified representation of MRCM attribute constraints; multiple widget bean instances can be
 * associated with the same widget model. The widget model does not support all cardinalities of the MRCM, only lower
 * bounds of 0 and 1 and upper bounds of 1 and * can be set. A third attribute indicates if the widget model is a
 * "catch-all", unsanctioned model, that did not originate from an MRCM constraint, an infrastructure model contributed
 * from Java code, or a regular one, converted from an actual rule.
 * 
 */
public abstract class WidgetModel implements Serializable {

	private static final long serialVersionUID = -944165643330640046L;

	protected static Set<String> copySet(final Set<String> source) {
		return UnrestrictedStringSet.INSTANCE == source ? UnrestrictedStringSet.INSTANCE : source;
	}

	public enum LowerBound {
		OPTIONAL, REQUIRED;
	}
	
	public enum UpperBound {
		SINGLE, MULTIPLE;
	}
	
	public enum ModelType {
		REGULAR, UNSANCTIONED, INFRASTRUCTURE;
	}
	
	private LowerBound lowerBound;
	private UpperBound upperBound;
	private ModelType modelType;
	
	/**
	 * Default constructor for serialization.
	 */
	protected WidgetModel() {
	}
	
	/**
	 * Creates a widget model with the specified cardinality and type.
	 * 
	 * @param lowerBound the widget model's lower bound (may not be {@code null})
	 * @param upperBound the widget model's upper bound (may not be {@code null})
	 * @param modelType the widget model type (may not be {@code null})
	 */
	protected WidgetModel(final LowerBound lowerBound, final UpperBound upperBound, final ModelType modelType) {
		this.lowerBound = checkNotNull(lowerBound, "lowerBound");
		this.upperBound = checkNotNull(upperBound, "upperBound");
		this.modelType = checkNotNull(modelType, "modelType");
	}

	/**
	 * @return {@code true} if the minimal cardinality is greater than 0 and the constraint strength is mandatory,
	 * {@code false} otherwise
	 */
	public boolean isRequired() {
		return LowerBound.REQUIRED.equals(lowerBound);
	}

	/**
	 * @return {@code true} if the cardinality upper bound is greater than 1 for the model, {@code false} otherwise
	 */
	public boolean isMultiple() {
		return UpperBound.MULTIPLE.equals(upperBound);
	}
	
	/**
	 * @return {@code true} if the widget model is unsanctioned, ie. not derived from an MRCM rule and allows the widest
	 * available set of options for its beans, {@code false} otherwise
	 */
	public boolean isUnsanctioned() {
		return ModelType.UNSANCTIONED.equals(modelType);
	}
	
	public boolean isInfrastructure() {
		return ModelType.INFRASTRUCTURE.equals(modelType);
	}
}