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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.index.admin.IndexAdmin;

/**
 * @since 4.7
 */
public final class DefaultIndex implements Index {

	private final IndexClient client;

	public DefaultIndex(IndexClient client) {
		this.client = checkNotNull(client, "client");
	}
	
	@Override
	public <T> T read(IndexRead<T> read) {
		try (DocSearcher searcher = client.searcher()) {
			return read.execute(searcher);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IndexException("Failed to execute index read", e);
		}
	}
	
	@Override
	public <T> T write(IndexWrite<T> write) {
		try (Writer writer = client.writer()) {
			return write.execute(writer);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IndexException("Failed to execute index write", e);
		}
	}
	
	@Override
	public String name() {
		return admin().name();
	}
	
	@Override
	public IndexAdmin admin() {
		return client.admin();
	}
	
}
