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

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.fsm.ITransition;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public abstract class ChallengeResponseNegotiator extends
    Negotiator<IChallengeResponse.State, IChallengeResponse.Event> implements IChallengeResponse
{
  private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

  private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

  private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

  public ChallengeResponseNegotiator(boolean initiator)
  {
    super(State.class, Event.class, State.INITIAL, State.SUCCESS, State.FAILURE, Event.START, Event.BUFFER, initiator);

    init(State.INITIAL, Event.START, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer NULL)
      {
        // Create and transmit challenge
        ByteBuffer challenge = context.getBuffer();
        createChallenge(context, challenge);
        context.transmitBuffer(challenge);

        // Set context state
        changeState(context, State.CHALLENGE);
      }
    });

    init(State.INITIAL, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer challenge)
      {
        // Handle challenge and transmit response
        ByteBuffer response = context.getBuffer();
        handleChallenge(context, challenge, response);
        context.transmitBuffer(response);

        // Set context state
        changeState(context, State.RESPONSE);
      }
    });

    init(State.CHALLENGE, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer response)
      {
        // Handle response
        boolean success = handleResponse(context, response);

        // Transmit acknowledgement
        ByteBuffer acknowledgement = context.getBuffer();
        acknowledgement.put(success ? ACKNOWLEDGE_SUCCESS : ACKNOWLEDGE_FAILURE);
        context.transmitBuffer(acknowledgement);
        ConcurrencyUtil.sleep(500);

        // Set context state
        changeState(context, success ? State.SUCCESS : State.FAILURE);
      }
    });

    init(State.RESPONSE, Event.BUFFER, new Transition()
    {
      @Override
      protected void execute(INegotiationContext context, ByteBuffer acknowledgement)
      {
        boolean success = acknowledgement.get() == ACKNOWLEDGE_SUCCESS;
        changeState(context, success ? State.SUCCESS : State.FAILURE);
        handleAcknowledgement(context, success);
      }
    });
  }

  /**
   * @since 2.0
   */
  public String getEncryptionAlgorithmName()
  {
    return encryptionAlgorithmName;
  }

  /**
   * @since 2.0
   */
  public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
  {
    this.encryptionAlgorithmName = encryptionAlgorithmName;
  }

  /**
   * @since 2.0
   */
  public byte[] getEncryptionSaltBytes()
  {
    return encryptionSaltBytes;
  }

  /**
   * @since 2.0
   */
  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
  {
    this.encryptionSaltBytes = encryptionSaltBytes;
  }

  /**
   * @since 2.0
   */
  public int getEncryptionIterationCount()
  {
    return encryptionIterationCount;
  }

  /**
   * @since 2.0
   */
  public void setEncryptionIterationCount(int encryptionIterationCount)
  {
    this.encryptionIterationCount = encryptionIterationCount;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(encryptionAlgorithmName, "encryptionAlgorithmName"); //$NON-NLS-1$
    checkState(encryptionSaltBytes, "encryptionSaltBytes"); //$NON-NLS-1$
    checkState(encryptionSaltBytes.length > 0, "encryptionSaltBytes"); //$NON-NLS-1$
    checkState(encryptionIterationCount > 0, "encryptionIterationCount"); //$NON-NLS-1$
  }

  @Override
  protected State getState(INegotiationContext subject)
  {
    return (State)subject.getState();
  }

  @Override
  protected void setState(INegotiationContext subject, State state)
  {
    subject.setState(state);
  }

  protected void createChallenge(INegotiationContext context, ByteBuffer challenge)
  {
    throw new UnsupportedOperationException();
  }

  protected void handleChallenge(INegotiationContext context, ByteBuffer challenge, ByteBuffer response)
  {
    throw new UnsupportedOperationException();
  }

  protected boolean handleResponse(INegotiationContext context, ByteBuffer response)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  protected void handleAcknowledgement(INegotiationContext context, boolean success)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class Transition implements ITransition<State, Event, INegotiationContext, ByteBuffer>
  {
    public final void execute(INegotiationContext context, State state, Event event, ByteBuffer buffer)
    {
      execute(context, buffer);
    }

    protected abstract void execute(INegotiationContext context, ByteBuffer buffer);
  }
}
