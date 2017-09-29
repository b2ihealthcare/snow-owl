/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.migrate;

import java.io.Serializable;

/**
 * @since 5.10.16
 */
public class MigrationResult implements Serializable {

	private final long failedCommitTimestamp;
	private final int processedCommits;
	private final int skippedCommits;
	private final Exception exception;
	
	MigrationResult(final long failedCommitTimestamp, final int processedCommits, final int skippedCommits, final Exception exception) {
		this.failedCommitTimestamp = failedCommitTimestamp;
		this.processedCommits = processedCommits;
		this.skippedCommits = skippedCommits;
		this.exception = exception;
	}
	
	public String getMessage() {
		if (failedCommitTimestamp == -1) {
			return String.format("Migration successfully completed. Processed %d and skipped %d commits.", processedCommits, skippedCommits);
		} else {
			return String.format(
					"Migration failed at commit %d. See log for details. Processed %d and skipped %d commits. Reason: %s.",
					failedCommitTimestamp, processedCommits, skippedCommits, exception != null ? exception.getMessage() : "Check logs for details.");
		}
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
	
}
