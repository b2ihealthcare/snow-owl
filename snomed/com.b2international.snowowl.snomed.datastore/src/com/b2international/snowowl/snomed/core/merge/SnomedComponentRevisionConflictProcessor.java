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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.b2international.index.revision.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.index.revision.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.index.revision.ChangedInSourceAndDetachedInTargetConflict;
import com.b2international.index.revision.Conflict;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
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
	
	@Override
	public Conflict handleChangedInSourceDetachedInTarget(ObjectId objectId, List<RevisionPropertyDiff> sourceChanges) {
		boolean conflicting = false;
		for (RevisionPropertyDiff sourceChange : sourceChanges) {
			if (SnomedDocument.Fields.RELEASED.equals(sourceChange.getProperty())) {
				conflicting = true;
			}
		}
		if (conflicting) {
			return new ChangedInSourceAndDetachedInTargetConflict(objectId, sourceChanges.stream().map(diff -> diff.convert(this)).collect(Collectors.toList()));
		} else {
			return super.handleChangedInSourceDetachedInTarget(objectId, sourceChanges);
		}
	}
	
	@Override
	public String convertPropertyValue(String property, String value) {
		if (SnomedDocument.Fields.EFFECTIVE_TIME.equals(property)) {
			if (EffectiveTimes.isUnset(value)) {
				return null;
			} else {
				final long effectiveTime = Long.parseLong(value);
				return Dates.formatByGmt(effectiveTime, DateFormats.SHORT);
			}
		} else {
			return super.convertPropertyValue(property, value);
		}
	}
	
	@Override
	public Conflict convertConflict(Conflict conflict) {
		if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			AddedInSourceAndDetachedInTargetConflict c = (AddedInSourceAndDetachedInTargetConflict) conflict;
			if ("member".equals(c.getAddedOnSource().type())) {
				return c.withFeatureName("referencedComponent");
			}
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			AddedInTargetAndDetachedInSourceConflict c = (AddedInTargetAndDetachedInSourceConflict) conflict;
			if ("member".equals(c.getDetachedOnSource().type())) {
				return c.withFeatureName("referencedComponent");
			}
		}
		return super.convertConflict(conflict);
	}

}
