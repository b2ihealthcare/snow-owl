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
package com.b2international.snowowl.datastore.server.store;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.snowowl.datastore.server.store.Types.ComplexData;
import com.b2international.snowowl.datastore.server.store.Types.State;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.datastore.store.Store;
import com.google.common.collect.Lists;

/**
 * @since 4.1
 */
public class IndexStorePerformanceTests {

	@Rule
	public ContiPerfRule rule = new ContiPerfRule();
	
	private Random rnd = new Random();
	private Store<ComplexData> store;
	private List<ComplexData> dataToStore = Collections.synchronizedList(Lists.<ComplexData>newLinkedList());

	@Before
	public void givenIndexStore() {
		this.store = new IndexStore<ComplexData>(IndexStoreTests.tmpDir(), ComplexData.class);
		dataToStore.addAll(generateData(1000));
	}
	
	@Ignore
	@Test
	@PerfTest(rampUp = 1000, warmUp = 1000, invocations = 1000)
	@Required(median = 25, percentile95 = 50)
	public void storingValuesSequentiallyShouldBeFast() throws Exception {
		final ComplexData value = dataToStore.remove(0);
		store.put(value.getId(), value);
		assertNotNull(store.get(value.getId()));
	}
	
	@Ignore
	@Test
	@PerfTest(rampUp = 1000, warmUp = 1000, invocations = 1000, threads = 8)
	@Required(median = 150, percentile95 = 250)
	public void storingValuesInParallelShouldBeFastButSlowerThanSeq() throws Exception {
		final ComplexData value = dataToStore.remove(0);
		store.put(value.getId(), value);
		assertNotNull(store.get(value.getId()));
	}
	
	private Collection<? extends ComplexData> generateData(int numberOfElements) {
		final Collection<ComplexData> result = newArrayList();
		for (int i = 0; i < numberOfElements; i++) {
			result.add(newRandomData());
		}
		return result;
	}

	private ComplexData newRandomData() {
		return new ComplexData(UUID.randomUUID().toString(), String.valueOf(rnd.nextLong()), randomState());
	}

	private State randomState() {
		switch (rnd.nextInt(2)) {
		case 0: return State.FAILED;
		case 1: return State.SCHEDULED;
		case 2: return State.RUNNING;
		default: throw new UnsupportedOperationException();
		}
	}
	
}
