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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class QueryLobsRequest extends CDOClientRequest<List<byte[]>>
{
  private Collection<byte[]> ids;

  public QueryLobsRequest(CDOClientProtocol protocol, Collection<byte[]> ids)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_LOBS);
    this.ids = ids;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(ids.size());
    for (byte[] id : ids)
    {
      out.writeByteArray(id);
    }
  }

  @Override
  protected List<byte[]> confirming(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    List<byte[]> result = new ArrayList<byte[]>(size);
    for (int i = 0; i < size; i++)
    {
      result.add(in.readByteArray());
    }

    return result;
  }
}
