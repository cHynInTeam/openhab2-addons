package org.openhab.binding.megad.internal;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.CommonTriggerEvents;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.events.ThingEventFactory;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MegaDServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String WEBAPP_ALIAS = "/megad";

    private Logger logger = LoggerFactory.getLogger(MegaDServlet.class);

    private HttpService httpService;
    private EventPublisher eventPublisher;
    private ThingRegistry thingRegistry;

    private Bridge findBridge(String mdid) {
        for (Thing thing : thingRegistry.getAll()) {
            if (thing.getUID().getBindingId().equals("megad") && thing.getUID().getId().equals(mdid)) {
                return (Bridge) thing;
            }
        }

        return null;
    }

    private Thing findThing(Bridge bridge, String thingId) {
        for (Thing thing : bridge.getThings()) {
            if (thing.getUID().getId().equals(thingId)) {
                return thing;
            }
        }
        return null;
    }

    private String getThingIdByPT(Integer pin) {
        return (pin > 13) ? "XP2" : "XP1";
    }

    private String getEventByM(String m) {
        if (m == null) {
            return CommonTriggerEvents.PRESSED;
        }
        if ("1".equals(m)) {
            return CommonTriggerEvents.RELEASED;
        }
        if ("2".equals(m)) {
            return CommonTriggerEvents.LONG_PRESSED;
        }
        return null;
    }

    private Channel findChannel(Thing thing, String channelId) {
        return thing.getChannel(channelId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // logger.info("MegaDServlet.get");
        // logger.info("Parameters:");
        // for (Entry<String, String[]> param : req.getParameterMap().entrySet()) {
        // logger.info(param.getKey() + " = " + param.getValue()[0]);
        // }

        Integer pt = Integer.valueOf(req.getParameter("pt"));
        String mdid = req.getParameter("mdid");
        String m = req.getParameter("m");

        Bridge bridge = findBridge(mdid);
        Thing thing = findThing(bridge, getThingIdByPT(pt));
        Channel channel = findChannel(thing, "p" + pt % 14);

        eventPublisher.post(ThingEventFactory.createTriggerEvent(getEventByM(m), channel.getUID()));

    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }

    public void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    public void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

    protected void activate(Map<String, Object> configProps) {
        try {
            Hashtable<String, String> props = new Hashtable<String, String>();
            httpService.registerServlet(WEBAPP_ALIAS, this, props, httpService.createDefaultHttpContext());
            logger.info("Started MegaD servlet at " + WEBAPP_ALIAS);
        } catch (NamespaceException e) {
            logger.error("Error during servlet startup", e);
        } catch (ServletException e) {
            logger.error("Error during servlet startup", e);
        }
    }

    protected void deactivate() {
        httpService.unregister(WEBAPP_ALIAS);
        logger.info("Stopped Basic UI");
    }

}
