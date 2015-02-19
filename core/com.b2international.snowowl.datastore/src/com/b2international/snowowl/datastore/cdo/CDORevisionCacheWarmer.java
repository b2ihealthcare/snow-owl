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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.google.common.collect.Sets;

/**
 * Job to prefetch all CDO specific resources for better performance. This job does not load the objects but updates the 
 * client side CDO revision cache.
 * @see CDORevisionCacheWarmerJob
 */
public enum CDORevisionCacheWarmer {

	/**
	 * The semantic cache warmer instance.
	 */
	INSTANCE;
	
	private static final String REVISION_CACHE_WARMER_JOB_EXTENSION_POINT_ID = 
			"com.b2international.snowowl.datastore.revisionCacheWarmerJob";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final Logger LOGGER = LoggerFactory.getLogger(CDORevisionCacheWarmer.class);
	
	private Collection<CDORevisionCacheWarmerJob> jobs;
	
	
	/**
	 * Synchronously schedules all available and registered {@link CDORevisionCacheWarmerJob semantic cache} warmer job.
	 */
	public void warmCache() {
		final Job globalWarmJob = new Job("Semantic cache warmer") {
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final Collection<CDORevisionCacheWarmerJob> warmerJobs = getWarmerJobs();
				final int size = getWarmerJobs().size();
				monitor.beginTask("Warming semantic cache...", size);
				final CountDownLatch latch = new CountDownLatch(size);
				
				for (final CDORevisionCacheWarmerJob warmer : warmerJobs) {
					warmer.addJobChangeListener(new JobChangeAdapter() {
						@Override public void done(final IJobChangeEvent event) {
							if (!event.getResult().isOK()) {
								LOGGER.warn("Error while executing semantic cache warming on " + warmer.getName());
							} else {
								LOGGER.info(warmer.getName() + " has successfully finished.");
							}
							latch.countDown();
							monitor.worked(1);
						}
					});
					warmer.setPriority(Job.INTERACTIVE);
					warmer.schedule();
				}
				
				try {
					latch.await();
				} catch (final InterruptedException e) {
					LOGGER.warn("Error while warming semantic caches.");
					return Status.CANCEL_STATUS;
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		
		globalWarmJob.setPriority(Job.INTERACTIVE);
		globalWarmJob.addJobChangeListener(new JobChangeAdapter() {
			@Override public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					LOGGER.info("Semantic cache warming successfully finished.");
				} else {
					LOGGER.warn("Semantic cache warming failed.");
				}
				final CDONet4jSession session = getSession();
				if (null != session) {
					//specifies the collection loading policy. this was set previously at CDO connection class.
					//was moved to here 07.03.2012.
					LOGGER.info("Altering collection loading policy for better CDO performance.");
					session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));
				}
			}
		});
		globalWarmJob.schedule();
	}

	/*returns with the session for the application.*/
	private CDONet4jSession getSession() {
		return getConnection().getSession();
	}

	/*returns with the connection service.*/
	private CDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(CDOConnection.class);
	}

	/*returns with the available and registered warmer jobs. loads if necessary.*/
	private Collection<CDORevisionCacheWarmerJob> getWarmerJobs() {
		if (null == jobs) {
			synchronized (CDORevisionCacheWarmer.class) {
				if (null == jobs)
					jobs = Sets.newHashSet(loadWarmerJobs());
			}
		}
		return jobs;
	}
	
	/*loads the available registered semantic cache warmers via Eclipse extension point*/
	private Iterable<CDORevisionCacheWarmerJob> loadWarmerJobs() {
		final Set<CDORevisionCacheWarmerJob> warmers = Sets.newHashSet();
		for (final IConfigurationElement element : getConfigurationElements()) {
			final CDORevisionCacheWarmerJob warmer = createWarmerJobSafe(element);
			if (null != warmer)
				warmers.add(warmer);
		}
		return warmers;
	}

	/*creates the executable CDO revision warmer job from the specified configuration element. returns with null if error occurred.*/
	private CDORevisionCacheWarmerJob createWarmerJobSafe(final IConfigurationElement element) {
		checkNotNull(element, "Configuration element argument should not be null.");
		try {
			final Object executableExtension = element.createExecutableExtension(CLASS_ATTRIBUTE);
			if (executableExtension instanceof CDORevisionCacheWarmerJob) {
				return (CDORevisionCacheWarmerJob) executableExtension;
			} else {
				throw new Exception("Executable extension should be a root resource identifier provider but was: " + executableExtension.getClass());
			}
		} catch (final Exception e) {
			final String id = element.getAttribute(ID_ATTRIBUTE);
			LOGGER.error("Error while creating executable root resource identifier provider with ID: '" + String.valueOf(id) + "'.", e);
			return null;
		}
	}
	
	/*returns with all available configuration elements for CDO revision cache warmer job extension point*/
	private IConfigurationElement[] getConfigurationElements() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(REVISION_CACHE_WARMER_JOB_EXTENSION_POINT_ID);
	}

}