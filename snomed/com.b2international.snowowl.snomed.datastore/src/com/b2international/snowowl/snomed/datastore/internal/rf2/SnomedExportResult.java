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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.io.Serializable;

/**
 * Represents the result of the SNOMED&nbsp;CT export.
 * 
 * @since 3.1
 */
public class SnomedExportResult implements Serializable {
	
	private static final long serialVersionUID = 7589566473306643358L;
	
	private static final String DEFAULT_SUCCESSFUL_MESSAGE = "SNOMED CT export successfully finished.";
	private static final String DEFAULT_EXCEPTION_MESSAGE = "An error occurred while exporting SNOMED CT components: exception during export.";
	private static final String DEFAULT_CANCELED_MESSAGE = "SNOMED CT export was canceled.";
	
	public enum Result {
		SUCCESSFUL,
		EXCEPTION,
		CANCELED
	}
	
	private Result result;
	private String message;
	
	public SnomedExportResult() {
		this.result = Result.SUCCESSFUL;
		this.message = DEFAULT_SUCCESSFUL_MESSAGE;
	}
	
	public SnomedExportResult(final Result result) {
		this.result = result;
		setDefaultMessage();
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(final Result result) {
		this.result = result;
		
		setDefaultMessage();
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(final String message) {
		this.message = message;
	}
	
	public void setResultAndMessage(final Result result, final String message) {
		this.result = result;
		this.message = message;
	}
	
	private void setDefaultMessage() {
		switch (result) {
		case SUCCESSFUL:
			this.message = DEFAULT_SUCCESSFUL_MESSAGE;
			break;
		case EXCEPTION:
			this.message = DEFAULT_EXCEPTION_MESSAGE;
			break;
		case CANCELED:
			this.message = DEFAULT_CANCELED_MESSAGE;
			break;
		}
	}

}