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

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;

/**
 * Difference processor for logging the consumed differences.
 * <p>This implementation does not produces any output.
 *
 */
public class LoggingDiffProcessor extends DiffProcessorImpl<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingDiffProcessor.class);
	
	private final Logger logger;
	private final DiffProcessor<String> delegate;
	
	public LoggingDiffProcessor() {
		this(LOGGER);
	}
	
	public LoggingDiffProcessor(@Nullable final Logger logger) {
		this.logger = null == logger ? LOGGER : logger;
		delegate = StringDiffProcessor.STRING_PROCESSOR;
	}

	@Override
	public Void processSingleValueAttributeChange(final SingleValueAttributeDiff diff) {
		logger.info(delegate.processSingleValueAttributeChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.processor.DiffProcessor#processManyValueAttributeChange(com.b2international.snowowl.emf.compare.diff.AttributeDiff)
	 */
	@Override
	public Void processManyValueAttributeChange(final AttributeDiff diff) {
		logger.info(delegate.processManyValueAttributeChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.processor.DiffProcessor#processSingleValueReferenceChange(com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff)
	 */
	@Override
	public Void processSingleValueReferenceChange(final SingleValueReferenceDiff diff) {
		logger.info(delegate.processSingleValueReferenceChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.emf.compare.diff.processor.DiffProcessor#processManyValueReferenceChange(com.b2international.snowowl.emf.compare.diff.ReferenceDiff)
	 */
	@Override
	public Void processManyValueReferenceChange(final ReferenceDiff diff) {
		logger.info(delegate.processManyValueReferenceChange(checkNotNull(diff, "diff")));
		return com.b2international.commons.Void.VOID;
	}

}