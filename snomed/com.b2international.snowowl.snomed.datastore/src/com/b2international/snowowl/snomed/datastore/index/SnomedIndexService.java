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
package com.b2international.snowowl.snomed.datastore.index;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;

/**
 * Index service for the SNOMED CT terminology on the client side. Responsible for 
 * managing the containing documents via the {@link IIndexUpdater updated} interface and for searching among them
 * via the {@link IIndexSearcher searcher} interface.
 * <p>
 * The interface only binds its type parameter and serves as an {@link ApplicationContext} service key.
 * 
 * @see IndexService
 */
public interface SnomedIndexService extends IIndexService<SnomedIndexEntry> { }