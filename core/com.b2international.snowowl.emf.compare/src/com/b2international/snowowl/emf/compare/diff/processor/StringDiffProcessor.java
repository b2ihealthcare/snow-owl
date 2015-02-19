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
package com.b2international.snowowl.emf.compare.diff.processor;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.Diff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;

/**
 * Processor for creating the string representation of a {@link Diff difference}.
 *
 */
public class StringDiffProcessor extends DiffProcessorImpl<String> {

	public static final DiffProcessor<String> STRING_PROCESSOR = new StringDiffProcessor();
	
	private StringDiffProcessor() {
		
	}
	
	@Override
	public String processSingleValueAttributeChange(final SingleValueAttributeDiff diff) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Attribute: '");
		sb.append(checkNotNull(diff, "diff").getChangedFeature().getName());
		sb.append("' from value: '");
		sb.append(diff.getOldValue());
		sb.append("' to value: '");
		sb.append(diff.getValue());
		sb.append("' on: '");
		sb.append(diff.getTarget());
		sb.append("' change: '");
		sb.append(diff.getChange());
		sb.append("'");
		return sb.toString();
	}

	@Override
	public String processManyValueAttributeChange(final AttributeDiff diff) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Attribute: '");
		sb.append(checkNotNull(diff, "diff").getChangedFeature().getName());
		sb.append("' value: '");
		sb.append(diff.getValue());
		sb.append("' on: '");
		sb.append(diff.getTarget());
		sb.append("' change: '");
		sb.append(diff.getChange());
		sb.append("'");
		return sb.toString();
	}

	@Override
	public String processSingleValueReferenceChange(final SingleValueReferenceDiff diff) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Reference: '");
		sb.append(checkNotNull(diff, "diff").getChangedFeature().getName());
		sb.append("' from value: '");
		sb.append(diff.getOldValue());
		sb.append("' to value: '");
		sb.append(diff.getValue());
		sb.append("' on: '");
		sb.append(diff.getTarget());
		sb.append("' change: '");
		sb.append(diff.getChange());
		sb.append("'");
		return sb.toString();
	}

	@Override
	public String processManyValueReferenceChange(final ReferenceDiff diff) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Reference: '");
		sb.append(checkNotNull(diff, "diff").getChangedFeature().getName());
		sb.append("' value: '");
		sb.append(diff.getValue());
		sb.append("' on: '");
		sb.append(diff.getTarget());
		sb.append("' change: '");
		sb.append(diff.getChange());
		sb.append("'");
		return sb.toString();
	}

}