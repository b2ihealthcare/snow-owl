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
package org.eclipse.emf.cdo.internal.common;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;

/**
 * @author Simon McDuff
 */
public class CDOQueryInfoImpl implements CDOQueryInfo
{
  protected String queryLanguage;

  protected String queryString;

  protected Object context;

  protected Map<String, Object> parameters = new HashMap<String, Object>();

  protected int maxResults = UNLIMITED_RESULTS;

  protected boolean legacyModeEnabled;

  protected CDOChangeSetData changeSetData;

  public CDOQueryInfoImpl(String queryLanguage, String queryString, Object context)
  {
    this.queryLanguage = queryLanguage;
    this.queryString = queryString;
    this.context = context;
  }

  public CDOQueryInfoImpl(CDODataInput in) throws IOException
  {
    queryLanguage = in.readString();
    queryString = in.readString();
    context = in.readCDORevisionOrPrimitiveOrClassifier();
    maxResults = in.readInt();
    legacyModeEnabled = in.readBoolean();

    if (in.readBoolean())
    {
      changeSetData = in.readCDOChangeSetData();
    }

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      String key = in.readString();
      Object object = in.readCDORevisionOrPrimitiveOrClassifier();
      parameters.put(key, object);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeString(queryLanguage);
    out.writeString(queryString);
    out.writeCDORevisionOrPrimitiveOrClassifier(context);
    out.writeInt(maxResults);
    out.writeBoolean(legacyModeEnabled);

    if (changeSetData != null)
    {
      out.writeBoolean(true);
      out.writeCDOChangeSetData(changeSetData);
    }
    else
    {
      out.writeBoolean(false);
    }

    out.writeInt(parameters.size());
    for (Entry<String, Object> entry : parameters.entrySet())
    {
      out.writeString(entry.getKey());
      out.writeCDORevisionOrPrimitiveOrClassifier(entry.getValue());
    }
  }

  public String getQueryString()
  {
    return queryString;
  }

  public String getQueryLanguage()
  {
    return queryLanguage;
  }

  public Map<String, Object> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  public Object getContext()
  {
    return context;
  }

  public CDOQueryInfoImpl setContext(Object context)
  {
    this.context = context;
    return this;
  }

  public void addParameter(String key, Object value)
  {
    parameters.put(key, value);
  }

  public int getMaxResults()
  {
    return maxResults;
  }

  public CDOQueryInfoImpl setMaxResults(int maxResults)
  {
    this.maxResults = maxResults;
    return this;
  }

  public boolean isLegacyModeEnabled()
  {
    return legacyModeEnabled;
  }

  public void setLegacyModeEnabled(boolean legacyModeEnabled)
  {
    this.legacyModeEnabled = legacyModeEnabled;
  }

  public CDOChangeSetData getChangeSetData()
  {
    return changeSetData;
  }

  public void setChangeSetData(CDOChangeSetData changeSetData)
  {
    this.changeSetData = changeSetData;
  }
}
