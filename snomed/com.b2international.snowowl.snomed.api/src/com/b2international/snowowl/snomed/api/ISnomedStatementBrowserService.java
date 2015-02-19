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
package com.b2international.snowowl.snomed.api;

import java.io.Serializable;

import com.b2international.snowowl.api.IComponentEdgeService;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;

/**
 * TODO review javadoc
 * 
 * SNOMED CT specific interface of the RESTful Statement Browser Service.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link ISnomedClientStatementBrowserService#getInboundStatements(Serializable) <em>Retrieve inbound statements</em>}</li>
 *   <li>{@link ISnomedClientStatementBrowserService#getOutboundStatements(Serializable) <em>Retrieve outbound statements</em>}</li>
 * </ul>
 * </p>
 * 
 */
public interface ISnomedStatementBrowserService extends IComponentEdgeService<ISnomedRelationship> {
	// Empty interface body
}