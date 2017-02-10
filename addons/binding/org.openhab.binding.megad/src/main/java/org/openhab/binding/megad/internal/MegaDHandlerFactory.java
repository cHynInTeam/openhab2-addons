/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.megad.internal;

import java.util.Set;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.megad.handler.MegaDBridgeHandler;
import org.openhab.binding.megad.handler.MegaDHandler;

import com.google.common.collect.Sets;

/**
 * The {@link MegaDHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author cHyn - Initial contribution
 */
public class MegaDHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Sets.union(MegaDHandler.SUPPORTED_THING_TYPES,
            MegaDBridgeHandler.SUPPORTED_THING_TYPES);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (MegaDBridgeHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            return new MegaDBridgeHandler((Bridge) thing);
        } else if (MegaDHandler.SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            return new MegaDHandler(thing);
        } else {
            return null;
        }
    }
}
