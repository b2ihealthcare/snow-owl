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
package com.b2international.snowowl.datastore.cdo;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * Mostly used in test environments, this class is responsible for generating unique storageKeys for {@link CDOObject}s and {@link RevisionDocument}s.
 * 
 * @since 6.6
 */
public enum StorageKeyAuthority {

	INSTANCE;

	private final AtomicLong storageKeys = new AtomicLong();

	public static long nextStorageKey() {
		return INSTANCE.storageKeys.getAndIncrement();
	}

}
