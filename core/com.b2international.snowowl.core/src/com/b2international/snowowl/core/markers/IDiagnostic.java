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

import java.util.Collection;

/**
 * Wraps and represents a diagnostic as an outcome of some activity. 
 */
public interface IDiagnostic {

	/**
	 * Returns {@code false} if the the represented outcome of some activity is finished with problem.
	 * Otherwise returns with {@code true}. 
	 * @return {@code false} if some problem occurred. Otherwise {@code true}.
	 */
	boolean isOk();
	
	/**
	 * Returns the {@link DiagnosticSeverity severity} of the current diagnostic.
	 * @return the severity of the diagnostic.
	 */
	DiagnosticSeverity getProblemMarkerSeverity();
	
	/**
	 * Returns with a collection of the children {@link IDiagnostic diagnostics} if any.
	 * @return the child diagnostics.
	 */
	Collection<IDiagnostic> getChildren();
	
	/**
	 * Returns with a human readable message of the current diagnostic.
	 * @return human readable description of the diagnostic.
	 */
	String getMessage();
	
	/**
	 * The unique identifier of the {@link IDiagnostic diagnostic}.
	 * @return unique identifier of the diagnostic.
	 */
	String getSource();
	
	/**
	 * Enumeration to represent the severity of a {@link IDiagnostic diagnostic} instance.
	 * The following types are available:
	 * <p>
	 * <ul>
	 *   <li>{@link DiagnosticSeverity#OK <em>OK</em>}</li>
	 *   <li>{@link DiagnosticSeverity#INFO <em>INFO</em>}</li>
	 *   <li>{@link DiagnosticSeverity#WARNING <em>WARNING</em>}</li>
	 *   <li>{@link DiagnosticSeverity#ERROR <em>ERROR</em>}</li>
	 *   <li>{@link DiagnosticSeverity#CANCEL <em>CANCEL</em>}</li>
	 * </ul>
	 * </p>
	 */
	public static enum DiagnosticSeverity {
		/**OK severity constant indicating that there was no error. (Code: <b>0x00</b>)
		 * @see DiagnosticSeverity
		 * */
		OK(0x00),
		/**Info severity constant indicating information only. (Code: <b>0x01</b>)
		 * @see DiagnosticSeverity
		 * */
		INFO(0x01),
		/**Warning severity constant indicating a warning. (Code: <b>0x02</b>)
		 * @see DiagnosticSeverity
		 * */
		WARNING(0x02),
		/**Error severity constant indicating an error state. (Code: <b>0x04</b>)
		 * @see DiagnosticSeverity
		 * */
		ERROR(0x04),
		/**Cancel severity constant indicating an error state. (Code: <b>0x08</b>)
		 * @see DiagnosticSeverity
		 * */
		CANCEL(0x08);
		
		private final int code;

		private DiagnosticSeverity(final int code) {
			this.code = code;
		}
		
		/**
		 * Returns with the error code. 
		 * @return the error code.
		 */
		public int getErrorCode() {
			return code;
		}
		
		public static DiagnosticSeverity valueOf(final int code) {
			for (DiagnosticSeverity value  : values()) {
				if (value.getErrorCode() == code)
					return value;
			}
			throw new IllegalArgumentException("Unexpected severity code: " + code);
		}
	}
	
}