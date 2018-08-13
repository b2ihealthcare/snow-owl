/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.IOperationLockTargetListener;

/**
 * A lock target listener implementation that outputs log messages whenever a lock target is added or removed.
 * 
 */
public class Slf4jOperationLockTargetListener implements IOperationLockTargetListener<DatastoreLockContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger("lock");
	
	@Override
	public void targetAcquired(final IOperationLockTarget target, final DatastoreLockContext context) {
		LOGGER.info("Lock acquired for {} ({}).", target, context.getDescription());
	}

	@Override
	public void targetReleased(final IOperationLockTarget target, final DatastoreLockContext context) {
		LOGGER.info("Lock released for {} ({}).", target, context.getDescription());
	}
}