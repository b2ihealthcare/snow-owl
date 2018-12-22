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
import java.io.InputStream;

/**
 * 
 */
public final class ReceiverInputWrapper extends InputStream {

	private final RpcInput delegate;

	/**
	 * 
	 * @param delegate
	 */
	public ReceiverInputWrapper(final RpcInput delegate) {
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(final long n) throws IOException {
		return delegate.skip(n);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return delegate.available();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		delegate.close();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(final int readlimit) {
		delegate.mark(readlimit);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		delegate.reset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return delegate.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return delegate.read();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(final byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		final byte[] actuallyRead = delegate.read(len);
		if (actuallyRead == null) {
			return -1;
		}
		System.arraycopy(actuallyRead, 0, b, off, actuallyRead.length);
		return actuallyRead.length;
	}
}