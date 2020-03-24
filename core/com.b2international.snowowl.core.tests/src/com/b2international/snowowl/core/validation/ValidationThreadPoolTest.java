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
package com.b2international.snowowl.core.validation;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;
import com.google.common.collect.Lists;

/**
 * @since 6.6
 */
public class ValidationThreadPoolTest {
	
	@Rule
	public final ValidationTestMethodNameRule methodName = new ValidationTestMethodNameRule();

	private static final long FAST_JOB_RUNTIME = 10L;
	private static final long NORMAL_JOB_RUNTIME = 100L;
	private static final long EXPENSIVE_JOB_RUNTIME = 300L;

	private static final int MAXIMUM_AMOUNT_OF_RUNNING_EXPENSIVE_JOBS = 2;
	private static final int MAXIMUM_AMOUNT_OF_RUNNING_NORMAL_JOBS = 4;

	private static final int VALIDATION_THREAD_COUNT = 6;

	private ValidationThreadPool pool;
	
	@Before
	public void init() {
		pool = new ValidationThreadPool(VALIDATION_THREAD_COUNT, MAXIMUM_AMOUNT_OF_RUNNING_EXPENSIVE_JOBS, MAXIMUM_AMOUNT_OF_RUNNING_NORMAL_JOBS);
	}
	
	@Test
	public void testConcurrentExpensiveJobs() {

		final IJobManager manager = Job.getJobManager();
		final Runnable expensiveRunnable = createValidatableRunnable(CheckType.EXPENSIVE, manager);

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.EXPENSIVE, expensiveRunnable));
		}

		Promise.all(validationPromises).getSync();
	}

	@Test
	public void testConcurrentFastJobs() {

		final IJobManager manager = Job.getJobManager();

		final Runnable fastRunnable = createValidatableRunnable(CheckType.FAST, manager);

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.FAST, fastRunnable));
		}

		Promise.all(validationPromises).getSync();
	}

	@Test
	public void TestConcurrentNormalJobs() {

		final IJobManager manager = Job.getJobManager();

		final Runnable fastRunnable = createValidatableRunnable(CheckType.NORMAL, manager);

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.NORMAL, fastRunnable));
		}

		Promise.all(validationPromises).getSync();
	}

	@Test
	public void testConcurrentFastJobsWithExpensiveOnes() {

		final IJobManager manager = Job.getJobManager();

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.FAST, createValidatableRunnable(CheckType.FAST, manager)));
			if (i % 3 == 0) {
				validationPromises.add(pool.submit(CheckType.EXPENSIVE, createValidatableRunnable(CheckType.EXPENSIVE, manager)));
			}
		}

		Promise.all(validationPromises).getSync();
	}
	
	@Test
	public void testConcurrentNormalJobsWithFastOnes() {

		final IJobManager manager = Job.getJobManager();

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.NORMAL, createValidatableRunnable(CheckType.NORMAL, manager)));
			if (i % 2 == 0) {
				validationPromises.add(pool.submit(CheckType.FAST, createValidatableRunnable(CheckType.FAST, manager)));
			}
		}

		Promise.all(validationPromises).getSync();
	}

	@Test
	public void test50JobsOfAllTypes() {

		final IJobManager manager = Job.getJobManager();

		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		validationPromises.add(pool.submit(CheckType.EXPENSIVE, createValidatableRunnable(CheckType.EXPENSIVE, manager)));
		for (int i = 0; i < 10; i++) {
			validationPromises.add(pool.submit(CheckType.FAST, createValidatableRunnable(CheckType.FAST, manager)));
			validationPromises.add(pool.submit(CheckType.NORMAL, createValidatableRunnable(CheckType.NORMAL, manager)));
			if (i % 3 == 0) {
				validationPromises.add(pool.submit(CheckType.EXPENSIVE, createValidatableRunnable(CheckType.EXPENSIVE, manager)));
			}
		}

		Promise.all(validationPromises).getSync();
	}

	private Runnable createValidatableRunnable(CheckType checkType, IJobManager manager) {
		long runTime = checkType == CheckType.EXPENSIVE ? EXPENSIVE_JOB_RUNTIME : CheckType.NORMAL == checkType ? NORMAL_JOB_RUNTIME : FAST_JOB_RUNTIME;
		return () -> {
			try {
				long runningExpensiveJobs = Arrays.asList(manager.find(CheckType.EXPENSIVE.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();
				long runningNormalJobs = Arrays.asList(manager.find(CheckType.NORMAL.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();
				long runningFastJobs = Arrays.asList(manager.find(CheckType.FAST.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();

				long allRunningJobs = runningExpensiveJobs + runningFastJobs + runningNormalJobs;

				assertTrue(allRunningJobs <= VALIDATION_THREAD_COUNT);
				System.out.println(
						String.format(
						"Number of expensive ones are [%d], number of normal ones [%d], number of fast ones [%d]",
						 runningExpensiveJobs, runningNormalJobs, runningFastJobs)
						);
				if (CheckType.EXPENSIVE == checkType) {
					assertTrue(runningExpensiveJobs <= MAXIMUM_AMOUNT_OF_RUNNING_EXPENSIVE_JOBS);
				}

				if (CheckType.NORMAL == checkType) {
					assertTrue(runningNormalJobs <= MAXIMUM_AMOUNT_OF_RUNNING_NORMAL_JOBS);
				}
				Thread.sleep(runTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		};
	}

}
