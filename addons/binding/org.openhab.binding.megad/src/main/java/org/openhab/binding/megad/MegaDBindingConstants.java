/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.megad;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link MegaDBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author cHyn - Initial contribution
 */
public class MegaDBindingConstants {

    public static final String BINDING_ID = "megad";

    // List of all Thing Type UIDs
    public final static ThingTypeUID MEGAD_2561 = new ThingTypeUID(BINDING_ID, "MegaD-2561");
    public final static ThingTypeUID MEGAD_7I7O_R = new ThingTypeUID(BINDING_ID, "MegaD-7I7O-R");

}
