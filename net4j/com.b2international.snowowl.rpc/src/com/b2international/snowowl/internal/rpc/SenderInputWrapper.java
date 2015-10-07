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
import java.util.Arrays;

/**
 * 
 */
public class SenderInputWrapper implements RpcInput {

	private final InputStream delegate;

	/**
	 * 
	 * @param delegate
	 */
	public SenderInputWrapper(final InputStream delegate) {
		this.delegate = delegate;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#read()
	 */
	public int read() throws IOException {
		return delegate.read();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#read(int)
	 */
	public byte[] read(final int len) throws IOException {
		final byte[] b = new byte[len];
		final int read = delegate.read(b);
		if (read < 0) {
			return null;
		}
		
		// Truncate to actually read size
		return Arrays.copyOf(b, read); 
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#skip(long)
	 */
	public long skip(final long n) throws IOException {
		return delegate.skip(n);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#available()
	 */
	public int available() throws IOException {
		return delegate.available();
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#close()
	 */
	public void close() throws IOException {
		delegate.close();
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#mark(int)
	 */
	public void mark(final int readlimit) {
		delegate.mark(readlimit);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#reset()
	 */
	public void reset() throws IOException {
		delegate.reset();
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.RpcInput#markSupported()
	 */
	public boolean markSupported() {
		return delegate.markSupported();
	}
}