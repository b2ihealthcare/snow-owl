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
package org.eclipse.emf.cdo.server.net4j;

import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor.AgentProtocol;
import org.eclipse.emf.cdo.spi.server.InternalFailoverParticipant;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A facility for monitoring a variable set of {@link InternalFailoverParticipant fail-over participant} repositories and electing,
 * as well as promoting, a {@link Type#MASTER master} repository among them.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class FailoverMonitor extends Container<AgentProtocol>
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.net4j.failoverMonitors";

  public static final String PROTOCOL_NAME = "failover"; //$NON-NLS-1$

  public static final short SIGNAL_PUBLISH_MASTER = 3;

  private String group;

  private List<AgentProtocol> agents = new ArrayList<AgentProtocol>();

  private AgentProtocol masterAgent;

  public FailoverMonitor()
  {
  }

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    checkInactive();
    this.group = group;
  }

  public AgentProtocol[] getElements()
  {
    synchronized (agents)
    {
      return agents.toArray(new AgentProtocol[agents.size()]);
    }
  }

  public AgentProtocol getMasterAgent()
  {
    synchronized (agents)
    {
      return masterAgent;
    }
  }

  public void registerAgent(AgentProtocol agent)
  {
    AgentProtocol newMasterAgent = null;
    AgentProtocol[] newAgents = null;

    synchronized (agents)
    {
      agents.add(agent);
      if (agents.size() == 1)
      {
        masterAgent = agent;
      }

      newMasterAgent = masterAgent;
      newAgents = getElements();
    }

    if (newMasterAgent != null)
    {
      publishNewMaster(newMasterAgent, newAgents);
    }

    fireElementAddedEvent(agent);
  }

  public void deregisterAgent(AgentProtocol agent)
  {
    AgentProtocol newMasterAgent = null;
    AgentProtocol[] newAgents = null;

    synchronized (agents)
    {
      if (!agents.remove(agent))
      {
        return;
      }

      if (masterAgent == agent)
      {
        if (agents.isEmpty())
        {
          masterAgent = null;
        }
        else
        {
          masterAgent = electNewMaster(agents);
        }
      }

      newMasterAgent = masterAgent;
      newAgents = getElements();
    }

    if (newMasterAgent != null)
    {
      publishNewMaster(newMasterAgent, newAgents);
    }

    fireElementRemovedEvent(agent);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(group, "group");
  }

  protected AgentProtocol electNewMaster(List<AgentProtocol> agents)
  {
    return agents.iterator().next();
  }

  private void publishNewMaster(final AgentProtocol masterAgent, AgentProtocol[] agents)
  {
    for (final AgentProtocol agent : agents)
    {
      try
      {
        new Request(agent, SIGNAL_PUBLISH_MASTER)
        {
          @Override
          protected void requesting(ExtendedDataOutputStream out) throws Exception
          {
            if (agent == masterAgent)
            {
              out.writeBoolean(true);
            }
            else
            {
              out.writeBoolean(false);
              out.writeString(masterAgent.getConnectorDescription());
              out.writeString(masterAgent.getRepositoryName());
            }
          }
        }.sendAsync();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * Provides a {@link FailoverMonitor fail-over monitor} for a given named fail-over group.
   *
   * @author Eike Stepper
   */
  public interface Provider
  {
    public FailoverMonitor getFailoverMonitor(String group);
  }

  /**
   * Creates {@link FailoverMonitor fail-over monitor} instances.
   *
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "net4j";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public FailoverMonitor create(String description) throws ProductCreationException
    {
      FailoverMonitor monitor = new FailoverMonitor();
      monitor.setGroup(description);
      return monitor;
    }
  }

  /**
   * An abstract base class for the {@link ServerProtocolFactory server-side protocol factories}
   * required by a {@link FailoverMonitor fail-over monitor}.
   *
   * @author Eike Stepper
   */
  public static abstract class AbstractServerProtocolFactory extends ServerProtocolFactory implements
      FailoverMonitor.Provider
  {
    private IManagedContainer container;

    protected AbstractServerProtocolFactory(String type)
    {
      this(type, IPluginContainer.INSTANCE);
    }

    protected AbstractServerProtocolFactory(String type, IManagedContainer container)
    {
      super(type);
      this.container = container;
    }

    public FailoverMonitor getFailoverMonitor(String group)
    {
      return (FailoverMonitor)container.getElement(FailoverMonitor.PRODUCT_GROUP, "net4j", group);
    }
  }

  /**
   * The monitor-side implementation of the {@link FailoverMonitor fail-over monitor} agent protocol.
   *
   * @author Eike Stepper
   */
  public static class AgentProtocol extends HeartBeatProtocol.Server
  {
    private FailoverMonitor.Provider failoverMonitorProvider;

    private FailoverMonitor failoverMonitor;

    private String connectorDescription;

    private String repositoryName;

    public AgentProtocol(Provider failOverMonitorProvider)
    {
      super(PROTOCOL_NAME);
      failoverMonitorProvider = failOverMonitorProvider;
    }

    @Override
    public String toString()
    {
      return connectorDescription + "/" + repositoryName;
    }

    protected FailoverMonitor getFailoverMonitor()
    {
      return failoverMonitor;
    }

    protected String getConnectorDescription()
    {
      return connectorDescription;
    }

    protected String getRepositoryName()
    {
      return repositoryName;
    }

    @Override
    protected void indicatingStart(ExtendedDataInputStream in) throws IOException
    {
      String group = in.readString();
      connectorDescription = in.readString();
      repositoryName = in.readString();

      failoverMonitor = failoverMonitorProvider.getFailoverMonitor(group);
      if (failoverMonitor == null)
      {
        throw new IllegalStateException("No monitor available for fail-over group " + group);
      }

      failoverMonitor.registerAgent(this);
      super.indicatingStart(in);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      failoverMonitor.deregisterAgent(this);
      super.doDeactivate();
    }

    /**
     * Creates {@link AgentProtocol fail-over agent protocol} instances.
     *
     * @author Eike Stepper
     */
    public static class Factory extends AbstractServerProtocolFactory
    {
      public Factory(IManagedContainer container)
      {
        super(PROTOCOL_NAME, container);
      }

      public Factory()
      {
        super(PROTOCOL_NAME);
      }

      public AgentProtocol create(String description) throws ProductCreationException
      {
        return new AgentProtocol(this);
      }
    }
  }

  /**
   * The monitor-side implementation of the {@link FailoverMonitor fail-over monitor} client protocol.
   *
   * @author Eike Stepper
   */
  public static class ClientProtocol extends SignalProtocol<Object>
  {
    public static final String PROTOCOL_NAME = "failover-client"; //$NON-NLS-1$

    public static final short SIGNAL_QUERY_REPOSITORY_INFO = 1;

    private FailoverMonitor.Provider failoverMonitorProvider;

    private FailoverMonitor failoverMonitor;

    public ClientProtocol(Provider failOverMonitorProvider)
    {
      super(PROTOCOL_NAME);
      failoverMonitorProvider = failOverMonitorProvider;
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case SIGNAL_QUERY_REPOSITORY_INFO:
        return new IndicationWithResponse(this, SIGNAL_QUERY_REPOSITORY_INFO, "QueryRepositoryInfo")
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            String group = in.readString();
            failoverMonitor = failoverMonitorProvider.getFailoverMonitor(group);
            if (failoverMonitor == null)
            {
              throw new IllegalStateException("No monitor available for fail-over group " + group);
            }
          }

          @Override
          protected void responding(ExtendedDataOutputStream out) throws Exception
          {
            AgentProtocol masterAgent = getMasterAgent();
            out.writeString(masterAgent.getConnectorDescription());
            out.writeString(masterAgent.getRepositoryName());
          }

          protected AgentProtocol getMasterAgent() throws InterruptedException
          {
            for (;;)
            {
              AgentProtocol masterAgent = failoverMonitor.getMasterAgent();
              if (masterAgent != null)
              {
                return masterAgent;
              }

              Thread.sleep(100L);
            }
          }
        };

      default:
        return super.createSignalReactor(signalID);
      }
    }

    /**
     * Creates {@link ClientProtocol fail-over client protocol} instances.
     *
     * @author Eike Stepper
     */
    public static class Factory extends AbstractServerProtocolFactory
    {
      public Factory(IManagedContainer container)
      {
        super(PROTOCOL_NAME, container);
      }

      public Factory()
      {
        super(PROTOCOL_NAME);
      }

      public ClientProtocol create(String description) throws ProductCreationException
      {
        return new ClientProtocol(this);
      }
    }
  }
}
