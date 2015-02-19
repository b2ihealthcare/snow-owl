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

import java.io.PrintStream;

import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;

/**
 * {@link DiffProcessor Node delta processor} implementation to print the changes 
 * to a {@link PrintStream stream}.
 *
 */
public class PrintStreamDiffProcessor extends DiffProcessorImpl<Void> {

	private final PrintStream stream;
	private final DiffProcessor<String> delegate;

	public PrintStreamDiffProcessor(final PrintStream stream) {
		this.stream = checkNotNull(stream, "stream");
		delegate = StringDiffProcessor.STRING_PROCESSOR;
	}

	@Override
	public Void processSingleValueAttributeChange(final SingleValueAttributeDiff diff) {
		stream.println(delegate.processSingleValueAttributeChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	@Override
	public Void processManyValueAttributeChange(final AttributeDiff diff) {
		stream.println(delegate.processManyValueAttributeChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	@Override
	public Void processSingleValueReferenceChange(final SingleValueReferenceDiff diff) {
		stream.println(delegate.processSingleValueReferenceChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	@Override
	public Void processManyValueReferenceChange(final ReferenceDiff diff) {
		stream.println(delegate.processManyValueReferenceChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

}