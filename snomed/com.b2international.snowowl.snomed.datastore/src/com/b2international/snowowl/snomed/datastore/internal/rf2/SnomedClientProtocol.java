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

import org.eclipse.net4j.signal.SignalProtocol;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;

public class SnomedClientProtocol extends SignalProtocol<Object> {

	public static final String PROTOCOL_NAME = "snowowl_snomed";
	
	private static SnomedClientProtocol INSTANCE;
	
	private SnomedClientProtocol() {
		super(PROTOCOL_NAME);
	}
	
	public static SnomedClientProtocol getInstance() {
		if (INSTANCE == null) {
			synchronized (SnomedClientProtocol.class) {
				if (INSTANCE == null) {
					INSTANCE = new SnomedClientProtocol();
					ApplicationContext.getInstance().getService(ICDOConnectionManager.class).openProtocol(INSTANCE);
				}
			}
		}
		return INSTANCE;
	}
	
}