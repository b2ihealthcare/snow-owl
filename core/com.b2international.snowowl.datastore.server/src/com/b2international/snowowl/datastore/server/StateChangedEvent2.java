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
package com.b2international.snowowl.datastore.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;
import org.eclipse.net4j.util.fsm.FiniteStateMachine;
import org.eclipse.net4j.util.fsm.FiniteStateMachine.StateChangedEvent;

import com.google.common.base.Preconditions;

/**
 * Represents a CDO object state change event as a workaround for unreachable {@link StateChangedEvent} class
 * used by the Net4j FSM.
 * @see StateChangedEvent
 */
@SuppressWarnings({ "rawtypes", "restriction" })
public class StateChangedEvent2 extends StateChangedEvent {

	/**
	 * Creates a new state changed event.
	 * @param object changed object.
	 * @param oldState the old state of the object.
	 * @param newState new state of the object.
	 * @return an event representing the state changed of the given CDO object.
	 */
	public static StateChangedEvent createInstance(final CDOObject object, final CDOState oldState, final CDOState newState) {
		return new StateChangedEvent2(CDOStateMachine.INSTANCE, Preconditions.checkNotNull(object, "CDO object argument cannot be null."), oldState, newState); 
	}
	
	/**
	 * Private constructor.
	 */
	@SuppressWarnings("unchecked")
	private StateChangedEvent2(final FiniteStateMachine finiteStateMachine, final Object subject, final Enum oldState, final Enum newState) {
		finiteStateMachine.super(subject, oldState, newState);
	}

	
}