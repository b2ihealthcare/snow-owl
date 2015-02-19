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
package com.b2international.snowowl.snomed.importer.net4j;

/**
 * Collects common constants related to the SNOMED&nbsp;CT component import mechanism.
 * 
 */
public interface SnomedImportProtocolConstants {

	/** The name of the SNOMED&nbsp;CT component import Net4j protocol. Value: <code>{@value}</code> */
	public static final String PROTOCOL_NAME = "snowowl_sct_import";

	public static final short SIGNAL_IMPORT_RF2 = 1000;
	
	public static final short SIGNAL_IMPORT_SUBSET = 1001;

	/** Use 64K buffer when transferring files */
	public static final int BUFFER_SIZE = 65536;
}