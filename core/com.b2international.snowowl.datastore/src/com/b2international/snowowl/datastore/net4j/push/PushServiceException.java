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
package com.b2international.snowowl.datastore.net4j.push;

/**
 * General exception class for reporting issues related to the push-subscribe service.
 * 
 * @since 2.8
 */
public class PushServiceException extends Exception {

	private static final long serialVersionUID = -8474513531401386173L;

	public PushServiceException() {
		super();
	}

	public PushServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PushServiceException(String arg0) {
		super(arg0);
	}

	public PushServiceException(Throwable arg0) {
		super(arg0);
	}

}