/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;

import com.b2international.index.revision.Hooks;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.Repository;

/**
 * Base {@link Repository} pre-commit hook. It allows terminology plugin developers to attach custom precommit hooks to the underlying
 * {@link Repository}. These hooks will be executed in order and applied to the current {@link StagingArea}.
 * 
 * @since 5.0
 */
public abstract class BaseRepositoryPreCommitHook implements Hooks.PreCommitHook {

	protected final Logger log;

	public BaseRepositoryPreCommitHook(Logger log) {
		this.log = log;
	}
	
	@Override
	public void run(StagingArea staging) {
		staging.read(index -> {
			updateDocuments(staging, index);
			return null;
		});
	}
	
	private final void updateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		log.info("Processing changes...");
		preUpdateDocuments(staging, index);
		doProcess(getChangeSetProcessors(staging, index), staging, index);
		postUpdateDocuments(staging, index);
		log.info("Processing changes successfully finished.");
	}

	protected final void doProcess(Collection<ChangeSetProcessor> changeSetProcessors, StagingArea staging, RevisionSearcher index) throws IOException {
		for (ChangeSetProcessor processor : changeSetProcessors) {
			log.trace("Processing {} changes...", processor.description());
			processor.process(staging, index);
			
			// register additions, deletions from the sub processor
			for (RevisionDocument revision : processor.getNewMappings().values()) {
				staging.stageNew(revision);
			}
			
			for (RevisionDocumentChange revisionChange : processor.getChangedMappings().values()) {
				staging.stageChange(((RevisionDocumentChange) revisionChange).getOldRevision(), ((RevisionDocumentChange) revisionChange).getNewRevision());
			}
			
			processor.getDeletions().forEach(staging::stageRemove);
		}
	}

	/**
	 * Subclasses may override this method to execute additional logic before the processing of the changeset.
	 * 
	 * @param staging - the staging area before committing it to the repository
	 * @param index 
	 * @throws IOException 
	 */
	protected void preUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
	}
	
	/**
	 * Return a list of {@link ChangeSetProcessor}s to process the commit changeset.
	 * 
	 * @param staging - the staging area before committing it to the repository
	 * @param index 
	 * @return
	 * @throws IOException
	 */
	protected Collection<ChangeSetProcessor> getChangeSetProcessors(StagingArea staging, RevisionSearcher index) throws IOException {
		return Collections.emptySet();
	}

	/**
	 * Subclasses may override this method to execute additional logic after the processing of the changeset, but before committing it.
	 * 
	 * @param staging - the staging area before committing it to the repository
	 * @param index 
	 * @throws IOException 
	 */
	protected void postUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
	}

}
