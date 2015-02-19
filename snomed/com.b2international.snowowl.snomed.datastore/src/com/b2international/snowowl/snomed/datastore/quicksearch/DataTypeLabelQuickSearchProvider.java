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
package com.b2international.snowowl.snomed.datastore.quicksearch;

import com.b2international.snowowl.datastore.quicksearch.QuickSearchProviderBase;

/**
 * Quick search provider for data type labels.
 * 
 */
public class DataTypeLabelQuickSearchProvider extends QuickSearchProviderBase {

	public static final String CONFIGURATION_DATA_TYPE = "DATA_TYPE";
	public static final String ID = "com.b2international.snowowl.snomed.datastore.quicksearch.DataTypeLabelQuickSearchProvider";
	private static final String NAME = "Data Type";
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getTerminologyComponentId() {
		return "UNSPECIFIED";
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}


}