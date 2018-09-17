/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.valueset;

import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Model class for the ValueSet/$validate-code service request response
 * 
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html#validate-code">FHIR:ValueSet:Operations:validate-code</a>
 * @since 6.9
 */
@JsonDeserialize(builder = ValidateCodeResult.Builder.class)
@JsonPropertyOrder({"result", "message", "display"})
public class ValidateCodeResult {
	
	//True if the concept details supplied are valid (1..1)
	private final boolean result;
	
	//Error details, if result = false. (0..1)
	//If this is provided when result = true, the message carries hints and warnings
	private final String message;
		
	//The valid display for this concept (0..1)
	private final String display;
	
	private ValidateCodeResult(final boolean result, 
			final String message, 
			final String display) {
		
		this.result = result;
		this.message = message;
		this.display = display;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public static Builder builder() {
		return new Builder();
	}
		
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<ValidateCodeResult> {
		
		private boolean result;
		private String message;
		private String display;
		
		public Builder result(final boolean result) {
			this.result = result;
			return this;
		}
		
		public Builder message(String message) {
			this.message = message;
			return this;
		}
		
		public Builder okMessage() {
			this.message = "OK";
			return this;
		}

		/**
		 * Builds a message for errors when value set is not found
		 * @param logicalId
		 * @return
		 */
		public Builder valueSetNotFoundMessage(LogicalId logicalId) {
			this.message("Could not find a valueset to check against: " + logicalId);
			return this;
		}

		/**
		 * Builds a message for errors when value set member is not found
		 * @param logicalId
		 * @return
		 */
		public Builder valueSetMemberNotFoundMessage(String system, String componentId) {
			this.message("Could not find a valueset member for: " + system + ":" + componentId);
			return this;
		}
		
		public Builder display(String display) {
			this.display = display;
			return this;
		}
		
		@Override
		public ValidateCodeResult doBuild() {
			return new ValidateCodeResult(result, message, display);
		}

	}

}
