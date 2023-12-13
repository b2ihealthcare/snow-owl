/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
@Doc(nested = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Similarity {
	
	private final String sourceValue;
	private final FloatList predictedValue;

	@JsonCreator
	private Similarity(@JsonProperty("source_value") String sourceValue, @JsonProperty("predicted_value") FloatList predictedValue) {
		this.sourceValue = sourceValue;
		this.predictedValue = predictedValue;
	}
	
	@JsonProperty("predicted_value")
	public FloatList getPredictedValue() {
		return predictedValue;
	}
	
	@JsonProperty("source_value")
	public String getSourceValue() {
		return sourceValue;
	}
	
	public static Similarity of(String sourceValue, float...predictedValues) {
		return of(sourceValue, PrimitiveLists.newFloatArrayList(predictedValues));
	}
	
	public static Similarity of(String sourceValue, FloatList predictedValue) {
		return new Similarity(sourceValue, predictedValue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sourceValue, predictedValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Similarity other = (Similarity) obj;
		return Objects.equals(sourceValue, other.sourceValue) 
				&& Objects.equals(predictedValue, other.predictedValue);
	}
	
}

