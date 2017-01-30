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

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.datastore.server.store.Types.Data;
import com.b2international.snowowl.datastore.server.store.Types.EmptyData;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.datastore.store.StoreException;
import com.google.common.io.Files;

/**
 * @since 4.1
 */
public class IndexStoreTests {

	private static final String KEY = "key";
	private static final String KEY2 = "key2";
	private static final String KEY3 = "key3";
	private static IndexStore<Data> store;
	private static IndexStore<EmptyData> emptyStore;
	
	@BeforeClass
	public static void givenIndexStore() {
		store = new IndexStore<Data>(Files.createTempDir(), Data.class);
		emptyStore = new IndexStore<EmptyData>(Files.createTempDir(), EmptyData.class);
	}
	
	@AfterClass
	public static void after() {
		if (store != null) {
			store.dispose();
			FileUtils.deleteDirectory(store.getDirectory());
		}
		if (emptyStore != null) {
			emptyStore.dispose();
			FileUtils.deleteDirectory(emptyStore.getDirectory());
		}
	}

	@Test(expected = StoreException.class)
	public void whenStoringEmptyData_ThenThrowException() throws Exception {
		emptyStore.put(KEY, new EmptyData());
	}

	@Test
	public void whenStoringData_ThenItCanBeRetrieved() throws Exception {
		final Data value = storeData();
		final Data actual = store.get(KEY);
		assertEquals(value, actual);
	}
	
	@Test
	public void whenStoringDataOnSameKey_ThenReplaceData() throws Exception {
		storeData();
		final Data newData = newData();
		store.put(KEY, newData);
		assertEquals(newData, store.get(KEY));
	}

	@Test
	public void whenRemovingDataFromStore_ThenItShouldBeRemoved() throws Exception {
		final Data value = storeData();
		final Data removed = store.remove(KEY);
		assertEquals(value, removed);
		assertNull(store.get(KEY));
	}
	
	@Test
	public void whenStoringMultipleData_ThenAllCanBeRetrievedViaValues() throws Exception {
		final Data value = storeData(KEY);
		final Data value2 = storeData(KEY2);
		assertThat(store.values()).containsOnly(value, value2);
	}
	
	@Test
	public void whenStoringMultipleData_ThenAllCanBeRetrievedViaKeys() throws Exception {
		final Data value = storeData(KEY);
		final Data value2 = storeData(KEY2);
		assertThat(store.get(newHashSet(KEY, KEY2))).containsOnly(value, value2);
		assertThat(store.get(newHashSet(KEY, KEY2, KEY3))).containsOnly(value, value2);
		assertThat(store.get(newHashSet(UUID.randomUUID().toString(), UUID.randomUUID().toString()))).isEmpty();
	}
	
	@Test
	public void whenClearingStoredData_ThenValuesShouldReturnEmptyCollection() throws Exception {
		whenStoringMultipleData_ThenAllCanBeRetrievedViaValues();
		store.clear();
		assertThat(store.values()).isEmpty();
	}
	
	@Test
	public void whenReplacingDataWithSameValue_ThenDoNothing() throws Exception {
		Data value = storeData();
		assertFalse(store.replace(KEY, value, value));
	}
	
	@Test
	public void whenReplacingDataWithInvalidOldValue_ThenDoNothing() throws Exception {
		storeData();
		final Data invalidOldValue = newData();
		assertFalse(store.replace(KEY, invalidOldValue, newData()));
	}
	
	@Test
	public void whenReplacingDataWithNewValue_ThenReplaceDataAndRetrieve() throws Exception {
		final Data value = storeData();
		final Data newData = newData();
		assertTrue(store.replace(KEY, value, newData));
		assertEquals(newData, store.get(KEY));
	}
	
	private Data storeData() {
		return storeData(KEY);
	}

	private Data storeData(String key) {
		final Data value = newData();
		store.put(key, value);
		return value;
	}

	private static Data newData() {
		return new Data(UUID.randomUUID().toString());
	}
	
}
