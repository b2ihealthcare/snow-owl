/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.IndexClientFactory;
import com.b2international.index.revision.RevisionFixtures.RevisionData;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * @since 7.16.0
 */
public class CommitWatermarkTest extends BaseRevisionIndexTest {

	private ListAppender<ILoggingEvent> appender;

	@Before
	public void setup() {
		appender = new ListAppender<>();
		appender.start();
		Logger log = (Logger) index().admin().log();
		log.addAppender(appender);
	}
	
	@Override
	public void after() {
		appender.stop();
		Logger log = (Logger) index().admin().log();
		log.detachAppender(appender);
		super.after();
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(RevisionData.class);
	}
	
	@Override
	protected Map<String, Object> getIndexSettings() {
		return Map.of(
			IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.DEFAULT_RESULT_WINDOW,
			IndexClientFactory.COMMIT_WATERMARK_LOW_KEY, 10,
			IndexClientFactory.COMMIT_WATERMARK_HIGH_KEY, 20
		);
	}
	
	@Test
	public void commitWatermarkLow() throws Exception {
		StagingArea staging = index().prepareCommit(MAIN);
		for (int i = 0; i < 11; i++) {
			staging.stageNew(new RevisionData(UUID.randomUUID().toString(), "1", "2"));
		}
		staging.commit(currentTime(), "test", "commitWatermarkLow");
		Assertions.assertThat(appender.list)
			.extracting(ILoggingEvent::getFormattedMessage)
			.contains("low commit watermark [10] exceeded in commit [MAIN - test - commitWatermarkLow] number of changes: 11");
	}
	
	@Test
	public void commitWatermarkHigh() throws Exception {
		StagingArea staging = index().prepareCommit(MAIN);
		for (int i = 0; i < 21; i++) {
			staging.stageNew(new RevisionData(UUID.randomUUID().toString(), "1", "2"));
		}
		staging.commit(currentTime(), "test", "commitWatermarkHigh");
		
		Assertions.assertThat(appender.list)
			.extracting(ILoggingEvent::getFormattedMessage)
			.contains("high commit watermark [20] exceeded in commit [MAIN - test - commitWatermarkHigh] number of changes: 21");
	}
	
}
