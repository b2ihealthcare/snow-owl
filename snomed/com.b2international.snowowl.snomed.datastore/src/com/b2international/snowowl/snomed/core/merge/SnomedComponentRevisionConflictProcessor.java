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
package com.b2international.snowowl.snomed.core.merge;

import java.util.Date;
import java.util.Objects;

import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public final class SnomedComponentRevisionConflictProcessor extends ComponentRevisionConflictProcessor {

	public SnomedComponentRevisionConflictProcessor() {
		super(ImmutableList.<IMergeConflictRule>builder()
//				.add(new SnomedRefsetMemberReferencingDetachedComponentRule())
//				.add(new SnomedLanguageRefsetMembersMergeConflictRule())
//				.add(new SnomedInvalidRelationshipMergeConflictRule())
				.build());
	}
	
	@Override
	public RevisionPropertyDiff handleChangedInSourceAndTarget(String revisionId, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange) {
		if (SnomedDocument.Fields.EFFECTIVE_TIME.equals(sourceChange.getProperty()) && !Objects.equals(sourceChange.getNewValue(), targetChange.getNewValue())) {
			if (EffectiveTimes.isUnset(sourceChange.getNewValue())) {
				return sourceChange;
			} else if (EffectiveTimes.isUnset(targetChange.getNewValue())) {
				return targetChange;
			} else {
				final Date sourceDate = EffectiveTimes.toDate(Long.parseLong(sourceChange.getNewValue()));
				final Date targetDate = EffectiveTimes.toDate(Long.parseLong(targetChange.getNewValue()));
				if (sourceDate.after(targetDate)) {
					return sourceChange;
				}
			}
		}
		return super.handleChangedInSourceAndTarget(revisionId, sourceChange, targetChange);
	}

}
