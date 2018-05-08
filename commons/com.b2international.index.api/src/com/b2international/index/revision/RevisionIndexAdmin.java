/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Map;

import org.slf4j.Logger;

import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;

/**
 * @since 6.5
 */
public final class RevisionIndexAdmin implements IndexAdmin {

	private final RevisionIndex index;
	private final IndexAdmin rawIndexAdmin;

	public RevisionIndexAdmin(RevisionIndex index, IndexAdmin rawIndexAdmin) {
		this.index = index;
		this.rawIndexAdmin = rawIndexAdmin;
	}
	
	@Override
	public Logger log() {
		return rawIndexAdmin.log();
	}

	@Override
	public boolean exists() {
		return rawIndexAdmin.exists();
	}

	@Override
	public void create() {
		rawIndexAdmin.create();
		// FIXME fix CDO vs non-CDO branch classes and move the whole branch infrastructure to this module first
//		index.branches().init();
	}

	@Override
	public void delete() {
		rawIndexAdmin.delete();
	}

	@Override
	public <T> void clear(Class<T> type) {
		rawIndexAdmin.clear(type);
	}

	@Override
	public Map<String, Object> settings() {
		return rawIndexAdmin.settings();
	}

	@Override
	public Mappings mappings() {
		return rawIndexAdmin.mappings();
	}

	@Override
	public String name() {
		return rawIndexAdmin.name();
	}

	@Override
	public String getTypeIndex(DocumentMapping mapping) {
		return rawIndexAdmin.getTypeIndex(mapping);
	}

	@Override
	public void close() {
		rawIndexAdmin.close();
	}

	@Override
	public void optimize(int maxSegments) {
		rawIndexAdmin.optimize(maxSegments);
	}

}
