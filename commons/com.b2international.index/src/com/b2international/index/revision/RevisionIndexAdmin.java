/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.index.reindex.RemoteInfo;
import org.slf4j.Logger;

import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.es.admin.IndexMapping;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.reindex.ReindexResult;
import com.b2international.index.es8.Es8Client;
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
	public EsClient client() {
		return rawIndexAdmin.client();
	}
	
	@Override
	public Es8Client es8Client() throws UnsupportedOperationException {
		return rawIndexAdmin.es8Client();
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
		this.index.admin().getIndexMapping().getMappings().putMapping(RevisionBranch.class);
		this.index.admin().getIndexMapping().getMappings().putMapping(Commit.class);
		rawIndexAdmin.create();
		index.branching().init();
	}

	@Override
	public void delete() {
		rawIndexAdmin.delete();
	}

	@Override
	public void clear(Collection<Class<?>> types) {
		rawIndexAdmin.clear(types);
	}

	@Override
	public Map<String, Object> settings() {
		return rawIndexAdmin.settings();
	}
	
	@Override
	public void updateSettings(Map<String, Object> newSettings) {
		rawIndexAdmin.updateSettings(newSettings);
	}

	@Override
	public IndexMapping getIndexMapping() {
		return rawIndexAdmin.getIndexMapping();
	}
	
	@Override
	public void updateMappings(Mappings mappings) {
		// ensure we have the necessary Mappings all the times
		mappings.putMapping(RevisionBranch.class);
		mappings.putMapping(Commit.class);
		rawIndexAdmin.updateMappings(mappings);
	}

	@Override
	public String name() {
		return rawIndexAdmin.name();
	}

	@Override
	public void optimize(int maxSegments) {
		rawIndexAdmin.optimize(maxSegments);
	}
	
	@Override
	public RefreshResponse refresh(String... indices) throws IOException {
		return rawIndexAdmin.refresh(indices);
	}
	
	@Override
	public ReindexResult reindex(String sourceIndex, String destinationIndex, RemoteInfo remoteInfo, boolean refresh) throws IOException {
		return rawIndexAdmin.reindex(sourceIndex, destinationIndex, remoteInfo, refresh);
	}
	
}
