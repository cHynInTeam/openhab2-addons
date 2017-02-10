/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.megad.handler;

import static org.openhab.binding.megad.MegaDBindingConstants.MEGAD_7I7O_R;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The {@link MegaDHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author cHyn - Initial contribution
 */
public class MegaDHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(MegaDHandler.class);

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(MEGAD_7I7O_R);

    public MegaDHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        MegaDBridgeHandler bridgeHandler = (MegaDBridgeHandler) getBridge().getHandler();
        bridgeHandler.handleCommand(getThing(), channelUID, command);
    }
}
