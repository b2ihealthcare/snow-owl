/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Blackburn (Broadcom Corp.) - ongoing development
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.io.*;

/**
 * This class should be used when there's a file already in the
 * destination and we don't want to lose its contents if a
 * failure writing this stream happens.
 * Basically, the new contents are written to a temporary location.
 * If everything goes OK, it is moved to the right place.
 * This class handles buffering of output stream contents.
 * 
 * Copied from org.eclipse.core.internal.localstore.SafeFileOutputStream
 */
public class SafeFileOutputStream extends OutputStream {
	protected File temp;
	protected File target;
	protected OutputStream output;
	protected boolean failed;
	protected static final String EXTENSION = ".bak"; //$NON-NLS-1$

	/**
	 * Creates an output stream on a file at the given location
	 * @param file The file to be written to
	 */
	public SafeFileOutputStream(File file) throws IOException {
		failed = false;
		target = file;
		temp = new File(target.getAbsolutePath() + EXTENSION);
		if (!target.exists()) {
			if (!temp.exists()) {
				output = new BufferedOutputStream(new FileOutputStream(target));
				return;
			}
			// If we do not have a file at target location, but we do have at temp location,
			// it probably means something wrong happened the last time we tried to write it.
			// So, try to recover the backup file. And, if successful, write the new one.
			copy(temp, target);
		}
		output = new BufferedOutputStream(new FileOutputStream(temp));
	}

	public void close() throws IOException {
		try {
			output.close();
		} catch (IOException e) {
			failed = true;
			throw e; // rethrow
		}
		if (failed)
			temp.delete();
		else
			commit();
	}

	protected void commit() throws IOException {
		if (!temp.exists())
			return;
		target.delete();
		copy(temp, target);
		temp.delete();
	}

	protected void copy(File sourceFile, File destinationFile) throws IOException {
		if (!sourceFile.exists())
			return;
		if (sourceFile.renameTo(destinationFile))
			return;
		InputStream source = null;
		OutputStream destination = null;
		try {
			source = new BufferedInputStream(new FileInputStream(sourceFile));
			destination = new BufferedOutputStream(new FileOutputStream(destinationFile));
			transferStreams(source, destination);
		} finally {
			try {
				if (source != null)
					source.close();
			} finally {
				//ignore secondary exception
			}
			try {
				if (destination != null)
					destination.close();
			} finally {
				//ignore secondary exception
			}
		}
	}

	public void flush() throws IOException {
		try {
			output.flush();
		} catch (IOException e) {
			failed = true;
			throw e; // rethrow
		}
	}

	public String getTempFilePath() {
		return temp.getAbsolutePath();
	}

	protected void transferStreams(InputStream source, OutputStream destination) throws IOException {
		byte[] buffer = new byte[8192];
		while (true) {
			int bytesRead = source.read(buffer);
			if (bytesRead == -1)
				break;
			destination.write(buffer, 0, bytesRead);
		}
	}

	public void write(int b) throws IOException {
		try {
			output.write(b);
		} catch (IOException e) {
			failed = true;
			throw e; // rethrow
		}
	}
}
