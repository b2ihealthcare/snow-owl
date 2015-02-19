/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A callback interface for handling large objects.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.uses {@link CDOLob} - - handles
 */
public interface CDOLobHandler
{
  /**
   * A callback method for handling a {@link CDOBlob binary large object}. The {@link CDOLob#getID() ID} and
   * {@link CDOLob#getSize() size} of the blob are passed by the caller. The implementor may return a
   * {@link OutputStream stream} that the blob content will be written to by the caller of this method, or
   * <code>null</code> to indicate that the content is not needed.
   */
  public OutputStream handleBlob(byte[] id, long size) throws IOException;

  /**
   * A callback method for handling a {@link CDOClob character large object}. The {@link CDOLob#getID() ID} and
   * {@link CDOLob#getSize() size} of the blob are passed by the caller. The implementor may return a {@link Writer
   * writer} that the blob content will be written to by the caller of this method, or <code>null</code> to indicate
   * that the content is not needed.
   */
  public Writer handleClob(byte[] id, long size) throws IOException;
}
