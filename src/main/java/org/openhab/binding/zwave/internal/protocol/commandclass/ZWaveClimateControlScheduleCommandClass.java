/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zwave.internal.protocol.commandclass;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import org.openhab.binding.zwave.internal.protocol.SerialMessage;
import org.openhab.binding.zwave.internal.protocol.SerialMessage.SerialMessageClass;
import org.openhab.binding.zwave.internal.protocol.SerialMessage.SerialMessagePriority;
import org.openhab.binding.zwave.internal.protocol.SerialMessage.SerialMessageType;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
import org.openhab.binding.zwave.internal.protocol.ZWaveEndpoint;
import org.openhab.binding.zwave.internal.protocol.ZWaveNode;
import org.openhab.binding.zwave.internal.protocol.ZWaveSerialMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Handles the climate control schedule command class.
 *
 * @author Chris Jackson
 */
@XStreamAlias("climateControlScheduleCommandClass")
public class ZWaveClimateControlScheduleCommandClass extends ZWaveCommandClass
        implements ZWaveCommandClassDynamicState {

    @XStreamOmitField
    private static final Logger logger = LoggerFactory.getLogger(ZWaveClimateControlScheduleCommandClass.class);

    private static final int CLIMATE_SCHEDULE_SET = 1;
    private static final int CLIMATE_SCHEDULE_GET = 2;
    private static final int CLIMATE_SCHEDULE_REPORT = 3;
    private static final int CLIMATE_SCHEDULE_CHANGED_GET = 4;
    private static final int CLIMATE_SCHEDULE_CHANGED_REPORT = 5;
    private static final int CLIMATE_SCHEDULE_OVERRIDE_SET = 6;
    private static final int CLIMATE_SCHEDULE_OVERRIDE_GET = 7;
    private static final int CLIMATE_SCHEDULE_OVERRIDE_REPORT = 8;

    /**
     * Creates a new instance of the ZWaveClimateControlCommandClass class.
     *
     * @param node the node this command class belongs to
     * @param controller the controller to use
     * @param endpoint the endpoint this Command class belongs to
     */
    public ZWaveClimateControlScheduleCommandClass(ZWaveNode node, ZWaveController controller, ZWaveEndpoint endpoint) {
        super(node, controller, endpoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandClass getCommandClass() {
        return CommandClass.CLIMATE_CONTROL_SCHEDULE;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ZWaveSerialMessageException
     */
    @Override
    public void handleApplicationCommandRequest(SerialMessage serialMessage, int offset, int endpoint)
            throws ZWaveSerialMessageException {
        logger.debug("NODE {}: Received CLIMATE_CONTROL_SCHEDULE command V{}", this.getNode().getNodeId(),
                this.getVersion());
        int command = serialMessage.getMessagePayloadByte(offset);
        switch (command) {
            case CLIMATE_SCHEDULE_REPORT:
            case CLIMATE_SCHEDULE_CHANGED_REPORT:
            case CLIMATE_SCHEDULE_OVERRIDE_REPORT:
                break;
            default:
                logger.warn(String.format("NODE %d: Unsupported Command %d for command class %s (0x%02X).",
                        this.getNode().getNodeId(), command, this.getCommandClass().getLabel(),
                        this.getCommandClass().getKey()));
        }
    }

    /**
     * Gets a SerialMessage with the CLIMATE_SCHEDULE_OVERRIDE_SET command
     *
     * @return the serial message.
     */
    public SerialMessage setOverrideMessage(OverrideType overrideType, int overrideValue) {
        logger.debug("NODE {}: Creating new message for command CLIMATE_SCHEDULE_OVERRIDE_SET", getNode().getNodeId());

        SerialMessage result = new SerialMessage(getNode().getNodeId(), SerialMessageClass.SendData,
                SerialMessageType.Request, SerialMessageClass.ApplicationCommandHandler, SerialMessagePriority.Set);
        ByteArrayOutputStream outputData = new ByteArrayOutputStream();
        outputData.write(getNode().getNodeId());
        outputData.write(4);
        outputData.write(getCommandClass().getKey());
        outputData.write(CLIMATE_SCHEDULE_OVERRIDE_SET);
        outputData.write(overrideType.ordinal());
        outputData.write(overrideValue);
        result.setMessagePayload(outputData.toByteArray());
        return result;
    }

    /**
     * Gets a SerialMessage with the CLOCK_GET command
     *
     * @return the serial message.
     */
    public SerialMessage getOverrideMessage() {
        logger.debug("NODE {}: Creating new message for command CLIMATE_SCHEDULE_OVERRIDE_GET", getNode().getNodeId());

        SerialMessage result = new SerialMessage(getNode().getNodeId(), SerialMessageClass.SendData,
                SerialMessageType.Request, SerialMessageClass.ApplicationCommandHandler, SerialMessagePriority.Get);
        ByteArrayOutputStream outputData = new ByteArrayOutputStream();
        outputData.write(getNode().getNodeId());
        outputData.write(2);
        outputData.write(getCommandClass().getKey());
        outputData.write(CLIMATE_SCHEDULE_OVERRIDE_GET);
        result.setMessagePayload(outputData.toByteArray());
        return result;
    }

    public enum OverrideType {
        NONE,
        TEMPORARY,
        PERMANENT
    }

    @Override
    public Collection<SerialMessage> getDynamicValues(boolean refresh) {
        // TODO Auto-generated method stub
        return null;
    }
}
