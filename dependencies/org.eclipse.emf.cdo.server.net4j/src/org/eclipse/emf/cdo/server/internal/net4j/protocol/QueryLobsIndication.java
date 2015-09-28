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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class QueryLobsIndication extends CDOServerReadIndication
{
  private List<byte[]> ids = new ArrayList<byte[]>();

  public QueryLobsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_LOBS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      ids.add(in.readByteArray());
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    getRepository().queryLobs(ids);
    out.writeInt(ids.size());
    for (byte[] id : ids)
    {
      out.writeByteArray(id);
    }
  }
}
