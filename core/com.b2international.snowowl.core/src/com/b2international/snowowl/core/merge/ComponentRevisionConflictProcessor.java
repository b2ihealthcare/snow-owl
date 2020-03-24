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
package com.b2international.snowowl.core.merge;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.time.TimeUtil;
import com.b2international.index.revision.Conflict;
import com.b2international.index.revision.RevisionBranchChangeSet;
import com.b2international.index.revision.RevisionConflictProcessor;
import com.b2international.index.revision.StagingArea;
import com.google.common.base.Stopwatch;

/**
 * @since 7.0
 */
public class ComponentRevisionConflictProcessor extends RevisionConflictProcessor.Default {
	
	private static final Logger LOG = LoggerFactory.getLogger(ComponentRevisionConflictProcessor.class);
	
	private final Collection<IMergeConflictRule> rules;

	public ComponentRevisionConflictProcessor(Collection<IMergeConflictRule> rules) {
		this.rules = Collections3.toImmutableList(rules);
	}
	
	@Override
	public List<Conflict> checkConflicts(StagingArea staging, RevisionBranchChangeSet fromChanges, RevisionBranchChangeSet toChanges) {
		LOG.info("Post-processing merge/rebase operation...");
		Stopwatch w = Stopwatch.createStarted();
		try {
			return rules.stream().flatMap(rule -> rule.validate(staging, fromChanges, toChanges).stream()).collect(Collectors.toList());
		} finally {
			LOG.info("Post-processing took {}", TimeUtil.toString(w));
		}
	}
	
}
