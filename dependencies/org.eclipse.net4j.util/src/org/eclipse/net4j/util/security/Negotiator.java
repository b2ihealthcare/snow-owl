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
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.fsm.FiniteStateMachine;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public abstract class Negotiator<STATE extends Enum<?>, EVENT extends Enum<?>> extends
    FiniteStateMachine<STATE, EVENT, INegotiationContext> implements INegotiator, INegotiationContext.Receiver
{
  private transient STATE initialState;

  private transient STATE successState;

  private transient STATE failureState;

  private transient EVENT startEvent;

  private transient EVENT bufferEvent;

  private boolean initiator;

  public Negotiator(Class<STATE> stateEnum, Class<EVENT> eventEnum, STATE initialState, STATE successState,
      STATE failureState, EVENT startEvent, EVENT bufferEvent, boolean initiator)
  {
    super(stateEnum, eventEnum);
    if (initialState == null)
    {
      throw new IllegalStateException("initialState == null"); //$NON-NLS-1$
    }

    if (successState == null)
    {
      throw new IllegalStateException("successState == null"); //$NON-NLS-1$
    }

    if (failureState == null)
    {
      throw new IllegalStateException("failureState == null"); //$NON-NLS-1$
    }

    if (startEvent == null)
    {
      throw new IllegalStateException("startEvent == null"); //$NON-NLS-1$
    }

    if (bufferEvent == null)
    {
      throw new IllegalStateException("bufferEvent == null"); //$NON-NLS-1$
    }

    this.initialState = initialState;
    this.successState = successState;
    this.failureState = failureState;
    this.startEvent = startEvent;
    this.bufferEvent = bufferEvent;
    this.initiator = initiator;
  }

  public STATE getInitialState()
  {
    return initialState;
  }

  public STATE getSuccessState()
  {
    return successState;
  }

  public STATE getFailureState()
  {
    return failureState;
  }

  public EVENT getBufferEvent()
  {
    return bufferEvent;
  }

  public EVENT getStartEvent()
  {
    return startEvent;
  }

  public boolean isInitiator()
  {
    return initiator;
  }

  public void negotiate(INegotiationContext context)
  {
    context.setReceiver(this);
    context.setState(initialState);
    if (initiator)
    {
      process(context, startEvent, null);
      postProcess(context);
    }
  }

  public void receiveBuffer(INegotiationContext context, ByteBuffer buffer)
  {
    process(context, bufferEvent, buffer);
    postProcess(context);
  }

  protected void postProcess(INegotiationContext context)
  {
    Enum<?> state = context.getState();
    if (state == successState)
    {
      handleSuccess(context);
    }
    else if (state == failureState)
    {
      handleFailure(context);
    }
  }

  /**
   * @since 2.0
   */
  protected void handleSuccess(INegotiationContext context)
  {
    context.setFinished(true);
  }

  /**
   * @since 2.0
   */
  protected void handleFailure(INegotiationContext context)
  {
    context.setFinished(false);
  }
}
