/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.similarity;

import java.util.Objects;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.floats.FloatList;
import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.3
 */
@Doc(nested = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Similarity {
	
	private final String source_value;
	private final FloatList predicted_value;

	@JsonCreator
	private Similarity(@JsonProperty("source_value") String sourceValue, @JsonProperty("predicted_value") FloatList predictedValue) {
		this.source_value = sourceValue;
		this.predicted_value = predictedValue;
	}
	
	public FloatList getPredicted_value() {
		return predicted_value;
	}
	
	public String getSource_value() {
		return source_value;
	}
	
	public static Similarity of(String sourceValue, float...predicatedValues) {
		return of(sourceValue, PrimitiveLists.newFloatArrayList(predicatedValues));
	}
	
	public static Similarity of(String sourceValue, FloatList predictedValue) {
		return new Similarity(sourceValue, predictedValue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(source_value, predicted_value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Similarity other = (Similarity) obj;
		return Objects.equals(source_value, other.source_value) 
				&& Objects.equals(predicted_value, other.predicted_value);
	}
	
}

