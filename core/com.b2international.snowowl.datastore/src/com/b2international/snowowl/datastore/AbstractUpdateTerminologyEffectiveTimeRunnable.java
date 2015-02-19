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
package com.b2international.snowowl.datastore;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.ICoreRunnableWithProgress;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.EffectiveTimes;

/**
 * Performs effective time property update on terminology components.
 * 
 * @since Snow&nbsp;Owl 3.0
 */
public abstract class AbstractUpdateTerminologyEffectiveTimeRunnable<M extends CDOObject> implements ICoreRunnableWithProgress {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdateTerminologyEffectiveTimeRunnable.class);

	private final CDOEditingContext context;
	private final Set<M> components;
	
	protected final Date targetEffectiveTime;

	public AbstractUpdateTerminologyEffectiveTimeRunnable(final CDOEditingContext context, final Set<M> components, final Date targetEffectiveTime) {
		this.context = context;
		this.components = components;
		this.targetEffectiveTime = targetEffectiveTime;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (null == monitor) {
			monitor = new NullProgressMonitor();
		}
		
		if (null == context) {
			LOGGER.warn(MessageFormat.format("Interrupting effective time property update process for {0} components. Reason: editing context was null.", 
					getTerminologyName()));
			monitor.done();
			
			return;
		}
		
		monitor.beginTask(MessageFormat.format("Updating effective time on unpublished {0} components...", getTerminologyName()), IProgressMonitor.UNKNOWN);
		
		try {
			for (final M component : components) {
				publish(component);
			}
			
			final SubMonitor subMonitor = SubMonitor.convert(monitor, MessageFormat.format("Updating effective time on unpublished {0} components...", getTerminologyName()), 1000)
					.newChild(1000, SubMonitor.SUPPRESS_ALL_LABELS);
			if (context.getTransaction().isDirty()) {
				context.commit(MessageFormat.format("Updated effective time to {0} on unpublished {1} components.", 
						EffectiveTimes.format(targetEffectiveTime), getTerminologyName()), subMonitor);
			}
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while updating effective time property on terminology components.", e);
		} finally {
			if (null != context) {
				context.close();
			}
			
			monitor.done();
		}
	}

	/**
	 * Updates the effective time property on the terminology specific component.
	 * 
	 * @param component the terminology specific component.
	 */
	protected abstract void publish(final M component);
	
	/**
	 * Gets the terminology specific name.
	 * 
	 * @return the name of the terminology.
	 */
	protected abstract String getTerminologyName();

}