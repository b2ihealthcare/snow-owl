/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * NOOP exporter.
 *
 */
public enum NoopExporter implements SnomedExporter {

	/**Shared NOOP exporter instance.*/
	INSTANCE;
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public String next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Implementation error.");
	}

	@Override
	public String getRelativeDirectory() {
		throw new UnsupportedOperationException("Implementation error.");
	}
	
	@Override
	public String getFileName() {
		throw new UnsupportedOperationException("Implementation error.");
	}

	@Override
	public String[] getColumnHeaders() {
		throw new UnsupportedOperationException("Implementation error.");
	}

	@Override
	public ComponentExportType getType() {
		throw new UnsupportedOperationException("Implementation error.");
	}

	@Override
	public SnomedExportContext getExportContext() {
		throw new UnsupportedOperationException("Implementation error.");
	}
	
	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public void execute() throws IOException {
	}

}