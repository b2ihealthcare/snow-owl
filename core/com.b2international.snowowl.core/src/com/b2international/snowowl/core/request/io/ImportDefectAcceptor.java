/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.io;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.core.runtime.ISafeRunnable;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.request.io.ImportDefect.ImportDefectType;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

/**
 * @since 7.12 
 */
public final class ImportDefectAcceptor {

	private static final int DEFAULT_MAX_DEFECTS = 100_000;
	
	private final String file;
	private final List<ImportDefect> defects = Lists.newArrayList();

	public ImportDefectAcceptor(String file) {
		this(file, DEFAULT_MAX_DEFECTS);
	}
	
	public ImportDefectAcceptor(String file, int maxDefects) {
		this.file = file;
	}

	public List<ImportDefect> getDefects() {
		return List.copyOf(defects);
	}
	
	public void error(String message) {
		new ImportDefectBuilder().error(message);
	}
	
	public void warn(String message) {
		new ImportDefectBuilder().warn(message);
	}
	
	public void info(String message) {
		new ImportDefectBuilder().info(message);
	}
	
	public ImportDefectBuilder on(String location) {
		return new ImportDefectBuilder().on(location);
	}
	
	/**
	 * @since 7.12
	 */
	public final class ImportDefectBuilder {
		
		private String location;
		private Supplier<Boolean> when;

		public ImportDefectBuilder on(String location) {
			this.location = location;
			return this;
		}

		public ImportDefectBuilder whenThrows(ISafeRunnable runnable) {
			return when(() -> {
				try {
					runnable.run();
					return false;
				} catch (Exception e) {
					return true;
				}
			});
		}
		
		public ImportDefectBuilder whenEqual(Object a, Object b) {
			return when(Objects.equals(a, b));
		}
		
		public ImportDefectBuilder whenNotEqual(Object a, Object b) {
			return when(!Objects.equals(a, b));
		}
		
		public ImportDefectBuilder whenBlank(String value) {
			return when(StringUtils.isEmpty(value));
		}
		
		public ImportDefectBuilder whenNaN(String value) {
			return when(!CharMatcher.inRange('0', '9').matchesAllOf(value));
		}
		
		public ImportDefectBuilder when(Boolean when) {
			return when(() -> when);
		}
		
		public ImportDefectBuilder when(Supplier<Boolean> when) {
			this.when = when;
			return this;
		}
		
		public void error(String message, Object...args) {
			error(String.format(message, args));
		}
		
		public void error(String message) {
			build(message, ImportDefectType.ERROR);
		}
		
		public void warn(String message, Object...args) {
			warn(String.format(message, args));
		}
		
		public void warn(String message) {
			build(message, ImportDefectType.WARNING);
		}
		
		public void info(String message, Object...args) {
			info(String.format(message, args));
		}
		
		public void info(String message) {
			build(message, ImportDefectType.INFO);
		}
		
		private void build(String message, ImportDefectType type) {
			if (when != null && when.get()) {
				defects.add(new ImportDefect(file, location, message, type));

				// Remove the earliest defect on overflow
				if (defects.size() > DEFAULT_MAX_DEFECTS) {
					defects.remove(0);
				}
			}
		}
	}
}
