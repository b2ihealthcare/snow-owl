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
package com.b2international.snowowl.internal.rpc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 
 */
public final class ReceiverOutputWrapper extends OutputStream {

	private final RpcOutput delegate;

	/**
	 * 
	 * @param delegate
	 */
	public ReceiverOutputWrapper(final RpcOutput delegate) {
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		delegate.write(b);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		delegate.write(b);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		byte[] relevantBytes = Arrays.copyOfRange(b, off, off + len);
		write(relevantBytes);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		delegate.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		delegate.close();
	}
}