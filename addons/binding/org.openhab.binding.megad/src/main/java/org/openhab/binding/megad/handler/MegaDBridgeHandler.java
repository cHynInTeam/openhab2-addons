package org.openhab.binding.megad.handler;

import static org.openhab.binding.megad.MegaDBindingConstants.MEGAD_2561;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class MegaDBridgeHandler extends BaseBridgeHandler implements Runnable {
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(MEGAD_2561);
    private final Logger logger = LoggerFactory.getLogger(MegaDBridgeHandler.class);

    protected String ip = null;
    protected BigDecimal poolInterval = null;

    private ScheduledFuture<?> pollingJob;

    public MegaDBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.info("initialize");

        ip = (String) this.getConfig().get("ip");
        poolInterval = (BigDecimal) this.getConfig().get("poolInterval");

        logger.info("ip: " + ip);
        logger.info("poolInterval: " + poolInterval.intValue());

        pollingJob = scheduler.scheduleWithFixedDelay(this, 0, poolInterval.intValue(), TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        logger.info("dispose");
        pollingJob.cancel(true);
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        Integer pin = Integer.valueOf(channelUID.getIdWithoutGroup().substring(1));

        logger.info("pin: " + pin);
    }

    public void handleCommand(Thing thing, ChannelUID channelUID, Command command) {
        Integer pin = Integer.valueOf(channelUID.getIdWithoutGroup().substring(1));

        if (thing.getUID().getId().equals("XP2")) {
            pin += 14;
        }

        logger.info("ThingId: " + thing.getUID().getId());
        logger.info("pin: " + pin);
        logger.info("Command: " + command.getClass().getName() + " (" + command.toString() + ")");

        try {
            if (command instanceof OnOffType) {
                URL url = new URL("http://" + ip + "/sec/?cmd=" + pin + ":" + (command == OnOffType.ON ? "1" : "0"));
                URLConnection connection = url.openConnection();
                connection.getInputStream();
            }
        } catch (IOException e) {
            logger.warn("Constructed url is not valid: {}", e.getMessage());
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }

    }

    public Thing getChildById(String id) {
        for (Thing thing : this.getThing().getThings()) {
            if (thing.getUID().getId().equals(id)) {
                return thing;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://" + ip + "/sec/?cmd=all");
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            this.updateStatus(ThingStatus.ONLINE);
            try (Scanner s = new Scanner(is).useDelimiter(";")) {
                for (int i = 0; s.hasNext(); i++) {
                    Thing thing = this.getThing();
                    int p = i;
                    String value = s.next();

                    if (p < 14) {
                        thing = getChildById("XP1");
                    } else if (p < 28) {
                        thing = getChildById("XP2");
                        p -= 14;
                    }

                    if (thing == null) {
                        continue;
                    }

                    State sValue;
                    if (value.startsWith("ON")) {
                        sValue = OnOffType.ON;
                    } else if (value.startsWith("OFF")) {
                        sValue = OnOffType.OFF;
                    } else {
                        sValue = new DecimalType(value);
                    }

                    Channel channel = thing.getChannel("p" + p);
                    if (channel != null) {
                        this.updateState(channel.getUID(), sValue);
                    }

                }
            }
            is.close();
        } catch (IOException e) {
            logger.warn("Constructed url is not valid: {}", e.getMessage());
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

}
