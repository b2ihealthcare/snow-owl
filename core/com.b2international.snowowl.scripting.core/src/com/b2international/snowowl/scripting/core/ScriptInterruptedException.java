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
package com.b2international.snowowl.scripting.core;

/**
 * Runtime exception for indicating Groovy script execution interruption.
 * <p><b>NOTE:&nbsp;</b>This exception has to occur only and if only the Groovy script execution has been
 * canceled by the user.</p>
 */
public class ScriptInterruptedException extends RuntimeException {
	private static final String MESSAGE = "User abort.";
	private static final long serialVersionUID = 7193362742604589552L;
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return MESSAGE;
	}
}