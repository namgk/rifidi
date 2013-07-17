/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators;

import obix.Bool;

public interface SunblindActuator extends Actuator {
	public static final String CONTRACT="iot:SunblindActuator";
	
	public static final String MOVE_DOWN_CONTRACT_NAME="moveDownValue";
	public static final String MOVE_DOWN_CONTRACT_HREF="moveDownValue";	
	public static final String MOVE_DOWN_CONTRACT = "<bool name='"+MOVE_DOWN_CONTRACT_NAME+"' href='"+MOVE_DOWN_CONTRACT_HREF+"' val='false'/>";
	public Bool moveDownValue();
	
	public static final String MOVE_UP_CONTRACT_NAME="moveUpValue";
	public static final String MOVE_UP_CONTRACT_HREF="moveUpValue";	
	public static final String MOVE_UP_CONTRACT = "<bool name='"+MOVE_UP_CONTRACT_NAME+"' href='"+MOVE_UP_CONTRACT_HREF+"' val='false'/>";
	public Bool moveUpValue();
	
//	public static final String DEDICATED_STOP_CONTRACT_NAME="dedicatedStopValue";
//	public static final String DEDICATED_STOP_CONTRACT_HREF="dedicatedStopValue";	
//	public static final String DEDICATED_STOP_CONTRACT = "<bool name='"+DEDICATED_STOP_CONTRACT_NAME+"' href='"+DEDICATED_STOP_CONTRACT_HREF+"' val='false'/>";
//	public Bool dedicatedStopValue();
//	
//	
//	public static final String STOP_STEP_UP_DOWN_CONTRACT_NAME="moveUpDownValue";
//	public static final String STOP_STEP_UP_DOWN_CONTRACT_HREF="moveUpDownValue";
//	public static final String STOP_STEP_UP_DOWN_CONTRACT = "<bool name='"+STOP_STEP_UP_DOWN_CONTRACT_NAME+"' href='"+STOP_STEP_UP_DOWN_CONTRACT_HREF+"' val='false'/>";
//	public Bool stopStepUpDownValue();
}


