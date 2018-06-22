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
package com.b2international.snowowl.snomed.reasoner.server.net4j;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import com.b2international.snowowl.snomed.reasoner.net4j.SnomedOntologyProtocol;

/**
 * Creates signal reactors to inbound SNOMED CT ontology export requests.
 * 
 */
public class SnomedOntologyServerProtocol extends SignalProtocol<Void> {

	/**
	 * Creates a new {@link SnomedOntologyServerProtocol} instance.
	 */
	public SnomedOntologyServerProtocol() {
		super(SnomedOntologyProtocol.PROTOCOL_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.SignalProtocol#createSignalReactor(short)
	 */
	@Override
	protected SignalReactor createSignalReactor(final short signalId) {
		switch (signalId) {
			case SnomedOntologyProtocol.EXPORT_SIGNAL_ID:
				return new SnomedOntologyExportIndication(this);
			default:
				return super.createSignalReactor(signalId);
		}
	}

	/**
	 * A server protocol factory which creates {@link SnomedOntologyServerProtocol} instances; used for registering the
	 * protocol using the {@code com.b2international.snowowl.datastore.server.protocolFactory} extension point.
	 * 
	 */
	public static final class Factory extends ServerProtocolFactory {
		
		/**
		 * Creates a new factory instance.
		 */
		public Factory() {
			super(SnomedOntologyProtocol.PROTOCOL_NAME);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.factory.IFactory#create(java.lang.String)
		 */
		@Override
		public Object create(final String description) throws ProductCreationException {
			return new SnomedOntologyServerProtocol();
		}
	}
}