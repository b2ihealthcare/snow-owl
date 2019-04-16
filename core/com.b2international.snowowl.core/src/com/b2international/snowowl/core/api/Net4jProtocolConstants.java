/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

/**
 * Constants used for our custom Net4j communication protocol.
 */
public abstract class Net4jProtocolConstants {
	
	//buffer size for file transfer, arbitrary number
	public static final short BUFFER_SIZE = 16384;

	//authentication protocol
	public static final short AUTHENTICATION_SIGNAL = 1;
	public static final short CREDENTIALS_SIGNAL = 2;
	
	//user management
	public static final short FETCH_USERS_SIGNAL = 21;  

	//ATC protocol
	public static final short ATC_IMPORT_SIGNAL = 101;
	public static final short ATC_EXPORT_SIGNAL = 102;
	
	//ICD-10 protocol
	public static final short ICD10_IMPORT_SIGNAL = 301;
	
	//ICD-10-AM protocol
	public static final short ICD10AM_IMPORT_SIGNAL = 601;
	
	//ICD-10-CM protocol
	public static final short ICD10CM_IMPORT_SIGNAL = 501;
	
	//UMLS protocol
	public static final short UMLS_IMPORT_SIGNAL = 401;
	public static final short UMLS_EXPORT_SIGNAL = 402;
	
	//LOINC protocol
	public static final short LOINC_IMPORT_SIGNAL = 701;
	public static final short LOINC_EXPORT_SIGNAL = 702;
	
	// Value set protocol
	public static final short VALUE_SET_EXCEL_IMPORT_SIGNAL = 801;
	public static final short VALUE_SET_EXCEL_EXPORT_SIGNAL = 802;
	public static final short VALUE_SET_UMLS_IMPORT_SIGNAL = 803;
	public static final short VALUE_SET_UMLS_EXPORT_SIGNAL = 804;
	
	//Mapping set protocol
	public static final short MAPPING_SET_EXCEL_IMPORT_SIGNAL = 1001;
	public static final short MAPPING_SET_EXCEL_EXPORT_SIGNAL = 1002;
	
	private Net4jProtocolConstants() {}
}