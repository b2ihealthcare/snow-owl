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
package com.b2international.snowowl.datastore.oplock.impl;

/**
 * Contains context descriptions used in {@link DatastoreLockContext}s.
 */
public abstract class DatastoreLockContextDescriptions {

	private DatastoreLockContextDescriptions() { }

	public static final String ROOT = "<<root>>";

	// XXX (apeteri): these descriptions are currently compared by content, exact reuse of messages can cause problems 
	public static final String DISPOSE_LOCK_MANAGER = "shutting down the lock manager";
	public static final String COMMIT = "committing changes";
	public static final String PREPARE = "waiting for a branch to be prepared";
	public static final String SYNCHRONIZE = "synchronizing changes";
	public static final String MULTI_SYNCHRONIZE = "synchronizing changes on one or more repositories";
	public static final String PROMOTE = "promoting changes";
	public static final String MULTI_PROMOTE = "promoting changes on one or more repositories";
	public static final String PROCESS_CHANGES = "waiting for changes to be processed";
	public static final String CONFIGURE_VERSION = "configuring the new version";
	public static final String CREATE_VERSION = "creating a new version";
	public static final String PERFORMING_ES_EXPORT = "performing an ES export";
	public static final String IMPORT = "importing release file content";
	public static final String GENERATE_ONTOLOGY = "generating ontology";
	public static final String GENERATE_BATCH_ONTOLOGY = "generating batch ontology";
	public static final String MAINTENANCE = "performing maintenance from the server console";
	public static final String CLASSIFY_WITH_REVIEW = "classifying the ontology and reviewing changes";
	public static final String SAVE_CLASSIFICATION_RESULTS = "persisting ontology changes";
	public static final String CLASSIFY = "classifying the ontology";
	public static final String REGISTER_NEW_CODE_SYSTEM = "registering a new code system version";
	public static final String CREATE_BACKUP = "creating a backup";
	public static final String CREATE_REPOSITORY_BACKUP = "creating a repository backup";
}
