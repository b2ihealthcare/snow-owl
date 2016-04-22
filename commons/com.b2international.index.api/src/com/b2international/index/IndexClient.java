/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import com.b2international.index.request.DeleteRequestBuilder;
import com.b2international.index.request.GetRequestBuilder;
import com.b2international.index.request.IndexRequestBuilder;
import com.b2international.index.request.SearchRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface to communicate with the index through various requests.
 * 
 * @since 4.7
 */
public interface IndexClient {

	GetRequestBuilder prepareGet(String type, String key);

	IndexRequestBuilder prepareIndex(String type, String key);

	IndexRequestBuilder prepareIndex(String type);
	
	DeleteRequestBuilder prepareDelete(String type, String key);

	SearchRequestBuilder prepareSearch(String type);

	SearchExecutor getDefaultExecutor(ObjectMapper mapper);

}
