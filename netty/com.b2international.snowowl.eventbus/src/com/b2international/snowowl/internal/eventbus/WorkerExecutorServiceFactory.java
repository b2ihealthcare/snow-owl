/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.primitives.Ints;

/**
 * @since 4.5.1
 */
public class WorkerExecutorServiceFactory implements ExecutorServiceFactory {

	@Override
	public ExecutorService createExecutorService(String description, int maxThreads) {
		final ThreadGroup group = new ThreadGroup(description);

		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final Thread thread = new Thread(group, r);
				thread.setName(description + "-" + thread.getId());
				thread.setDaemon(true);
				return thread;
			}
		};

		final ExecutorScalingQueue<Runnable> queue = new ExecutorScalingQueue<Runnable>();
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(
			Ints.constrainToRange(Runtime.getRuntime().availableProcessors(), 2, maxThreads), maxThreads, 
			1L, TimeUnit.MINUTES,
			queue, 
			threadFactory,
			new ForceQueuePolicy()
		);
		queue.executor = executor;
		return executor;
	}
	
	/*Elaticsearch maintained class, copied from EsExecutors*/
	static class ExecutorScalingQueue<E> extends LinkedTransferQueue<E> {

        ThreadPoolExecutor executor;

        ExecutorScalingQueue() {
        }

        @Override
        public boolean offer(E e) {
            // first try to transfer to a waiting worker thread
            if (!tryTransfer(e)) {
                // check if there might be spare capacity in the thread
                // pool executor
                int left = executor.getMaximumPoolSize() - executor.getCorePoolSize();
                if (left > 0) {
                    // reject queuing the task to force the thread pool
                    // executor to add a worker if it can; combined
                    // with ForceQueuePolicy, this causes the thread
                    // pool to always scale up to max pool size and we
                    // only queue when there is no spare capacity
                    return false;
                } else {
                    return super.offer(e);
                }
            } else {
                return true;
            }
        }

    }
	
	/*Elaticsearch maintained class, copied from EsExecutors*/
    /**
     * A handler for rejected tasks that adds the specified element to this queue,
     * waiting if necessary for space to become available.
     */
    static class ForceQueuePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                // force queue policy should only be used with a scaling queue
                assert executor.getQueue() instanceof ExecutorScalingQueue;
                executor.getQueue().put(r);
            } catch (final InterruptedException e) {
                // a scaling queue never blocks so a put to it can never be interrupted
                throw new AssertionError(e);
            }
        }

    }
	
}
