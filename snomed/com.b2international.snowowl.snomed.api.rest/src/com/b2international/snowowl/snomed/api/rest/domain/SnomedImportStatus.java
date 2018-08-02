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
package com.b2international.snowowl.snomed.api.rest.domain;

import static com.google.common.collect.EnumBiMap.create;
import static com.google.common.collect.Maps.unmodifiableBiMap;

import java.util.EnumMap;

import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.google.common.collect.BiMap;

/**
 * @since 1.0
 */
public enum SnomedImportStatus {
	WAITING_FOR_FILE,
	RUNNING,
	COMPLETED,
	FAILED;
	
	/**
	 * Returns with the {@link ISnomedImportConfiguration.ImportStatus import status} instance
	 * that stands for the {@link SnomedImportStatus} argument.
	 * @param status the import status for the SNOMED&nbsp;CT import process.
	 * @return the mapped import status instance.
	 */
	public static ImportStatus getImportStatus(final SnomedImportStatus status) {
		return STATUS_MAPPING.get(status);
	}
	
	/**
	 * Returns with the {@link SnomedImportStatus import status} instance
	 * that stands for the {@link ISnomedImportConfiguration.ImportStatus } argument.
	 * @param status the import status for the SNOMED&nbsp;CT import process.
	 * @return the mapped import status instance.
	 */
	public static SnomedImportStatus getImportStatus(final ImportStatus status) {
		return STATUS_MAPPING.inverse().get(status);
	}
	
	private static final BiMap<SnomedImportStatus, ImportStatus> STATUS_MAPPING = // 
			unmodifiableBiMap(create(new EnumMap<SnomedImportStatus, ImportStatus>(SnomedImportStatus.class) {
		
		private static final long serialVersionUID = 1272336677673814738L;

		{
			put(WAITING_FOR_FILE, ImportStatus.WAITING_FOR_FILE);
			put(COMPLETED, ImportStatus.COMPLETED);
			put(FAILED, ImportStatus.FAILED);
			put(RUNNING, ImportStatus.RUNNING);
		}
		
	}));
}