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
package com.b2international.snowowl.snomed.datastore.server.export;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;

public class SnomedServerProtocol extends SignalProtocol<Object> {

	public static final String PROTOCOL_NAME = "snowowl_snomed";
	
	public SnomedServerProtocol() {
		super(PROTOCOL_NAME);
	}

	@Override
	protected SignalReactor createSignalReactor(final short signalID) {
		switch (signalID) {
			case Net4jProtocolConstants.REFSET_TO_DSV_SIGNAL:
				return new SnomedRefSetDSVExportServerIndication(this);
			case Net4jProtocolConstants.SNOMED_EXPORT_REFSET_TO_EXCEL_SIGNAL:
				return new SnomedRefSetExcelExportServerIndication(this);
			default:
				return super.createSignalReactor(signalID);
		}
	}

	public static final class Factory extends ServerProtocolFactory {
		public Factory() {
			super(PROTOCOL_NAME);
		}

		@Override
		public Object create(final String description) throws ProductCreationException {
			return new SnomedServerProtocol();
		}
	}
}