/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Test;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;
import com.google.common.collect.Lists;

/**
 * @since 6.6
 */
public class ValidationThreadPoolTest {
	
	private static final int FAST_VALIDATION_THREAD_COUNT = 6;
	private static final int MEDIUM_VALIDATION_THREAD_COUNT = 4;
	private static final int EXPENSIVE_VALIDATION_THREAD_COUNT = 3;
	
	@Test
	public void testConcurrentExpensiveJobs() {
		final ValidationThreadPool pool = new ValidationThreadPool(EXPENSIVE_VALIDATION_THREAD_COUNT);
		
		final IJobManager manager = Job.getJobManager();
		final Runnable expensiveRunnable = () -> {
			try {
				Job[] expensiveJobs = manager.find(CheckType.EXPENSIVE.getName());
				int jobsInManager = expensiveJobs.length;
				long runningJobs = Arrays.asList(expensiveJobs).stream().filter(job -> job.getState() == Job.RUNNING).count();
				assertEquals(1L, runningJobs);
				Thread.sleep(2000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		final Promise<Object> expensivePromise1 = pool.submit(CheckType.EXPENSIVE, expensiveRunnable);
		final Promise<Object> expensivePromise2 = pool.submit(CheckType.EXPENSIVE, expensiveRunnable);
		// there should be no running jobs
		assertEquals(0, Arrays.asList(manager.find(CheckType.EXPENSIVE)).stream().filter(job -> job.getState() == Job.RUNNING).count());
		Promise.all(expensivePromise1, expensivePromise2).getSync();
	}
	
	@Test
	public void testConcurrentFastJobs() {
	
		final ValidationThreadPool pool = new ValidationThreadPool(FAST_VALIDATION_THREAD_COUNT);
		
		final IJobManager manager = Job.getJobManager();
		final Runnable fastRunnable = () -> {
			try {
				Job[] fastJobs = manager.find(CheckType.FAST.getName());
				int jobsInManager = fastJobs.length;
				long runningJobs = Arrays.asList(fastJobs).stream().filter(job -> job.getState() == Job.RUNNING).count();
				System.err.println(runningJobs);
				assertEquals(1L, runningJobs);
				Thread.sleep(2000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		
		List<Promise<Object>> fastValidationPromises = Lists.newArrayList();
		for (int i = 0; i < FAST_VALIDATION_THREAD_COUNT; i++) {
			fastValidationPromises.add(pool.submit(CheckType.FAST, fastRunnable));
		}
		// there should be no running jobs
		assertEquals(0, Arrays.asList(manager.find(CheckType.EXPENSIVE)).stream().filter(job -> job.getState() == Job.RUNNING).count());
		Promise.all(fastValidationPromises).getSync();
	}
	
}
