/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRequest.class);

  private CDOView view;

  private AbstractQueryIterator<?> queryResult;

  public QueryRequest(CDOClientProtocol protocol, CDOView view, AbstractQueryIterator<?> queryResult)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY);
    this.view = view;
    this.queryResult = queryResult;
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return (InternalCDOView)view;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(view.getViewID());
    ((CDOQueryInfoImpl)queryResult.getQueryInfo()).write(out);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    int queryID = in.readInt();
    queryResult.setQueryID(queryID);
    CDOQueryQueue<Object> resultQueue = queryResult.getQueue();

    boolean xrefs = queryResult.getQueryInfo().getQueryLanguage().equals(CDOProtocolConstants.QUERY_LANGUAGE_XREFS);

    try
    {
      int numberOfObjectsReceived = 0;
      while (in.readBoolean())
      {
        Object element;
        if (xrefs)
        {
          CDOIDReference delegate = in.readCDOIDReference();
          element = new CDOObjectReferenceImpl(view, delegate);
        }
        else
        {
          element = in.readCDORevisionOrPrimitive();
        }

        resultQueue.add(element);
        numberOfObjectsReceived++;
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Query executed [{0} elements received]", numberOfObjectsReceived); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ex)
    {
      resultQueue.setException(ex);
    }
    catch (Throwable throwable)
    {
      resultQueue.setException(new RuntimeException(throwable.getMessage(), throwable));
    }
    finally
    {
      resultQueue.close();
    }

    return true;
  }
}
