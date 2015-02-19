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
package com.b2international.snowowl.snomed.importer.rf2.net4j;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.GZIPStreamWrapper;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import com.b2international.snowowl.snomed.importer.net4j.SnomedImportProtocolConstants;

/**
 * 
 */
public class SnomedImportServerProtocol extends SignalProtocol<Void> {

	public SnomedImportServerProtocol() {
		super(SnomedImportProtocolConstants.PROTOCOL_NAME);
		setStreamWrapper(new GZIPStreamWrapper());
	}

	@Override protected SignalReactor createSignalReactor(final short signalID) {
		switch (signalID) {
		case SnomedImportProtocolConstants.SIGNAL_IMPORT_RF2:
			return new SnomedImportIndication(this);
		case SnomedImportProtocolConstants.SIGNAL_IMPORT_SUBSET:
			return new SnomedSubsetImportIndication(this);
		default:
			return super.createSignalReactor(signalID);
		}
	}

	public static final class Factory extends ServerProtocolFactory {

		public Factory() {
			super(SnomedImportProtocolConstants.PROTOCOL_NAME);
		}

		@Override public Object create(final String description) throws ProductCreationException {
			return new SnomedImportServerProtocol();
		}
	}
}