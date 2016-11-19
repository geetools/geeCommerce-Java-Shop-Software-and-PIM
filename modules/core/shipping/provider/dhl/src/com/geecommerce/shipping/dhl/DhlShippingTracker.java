package com.geecommerce.shipping.dhl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import com.geecommerce.core.App;
import com.geecommerce.shipping.AbstractShippingTracker;
import com.geecommerce.shipping.annotation.ShippingTracker;
import com.geecommerce.shipping.model.ShippingEvent;
import com.dhl.KnownTrackingRequest;
import com.dhl.ShipmentTrackingErrorResponse;
import com.dhl.TrackingResponse;
import com.dhl.datatypes.AWBInfo;
import com.dhl.datatypes.LevelOfDetails;
import com.dhl.datatypes.ShipmentEvent;

@ShippingTracker
public class DhlShippingTracker extends AbstractShippingTracker {
    private DhlService dhlService = new DhlService();

    @Override
    public String getCode() {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    @Override
    public String isMatch(String trackingNumber) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    @Override
    public String getUrl(String trackingNumber) {
        return null;
    }

    @Override
    public List<ShippingEvent> getShipmentEvents(String trackingNumber) {
        try {
            String request = dhlService.marshal(getTrackingRequest(trackingNumber));
            HttpURLConnection connection = dhlService.createUrlConnection();
            dhlService.sendRequestToXmlPi(request, connection);
            String response = dhlService.readResponseFromXmlPi(connection);
            if (!dhlService.isErrorResponse(response, "res:ErrorResponse")) {
                TrackingResponse trackingResponse = (TrackingResponse) dhlService.unmarshal(response, new Class[] { TrackingResponse.class });
                // TODO: check for fault
                List<ShippingEvent> shippingEvents = new ArrayList<>();
                // Presume that we get info only for one shipment;
                if (trackingResponse.getAWBInfo().size() > 0) {
                    AWBInfo info = trackingResponse.getAWBInfo().get(0);
                    for (ShipmentEvent shipmentEvent : info.getShipmentInfo().getShipmentEvent()) {
                        ShippingEvent event = App.get().getModel(ShippingEvent.class);
                        event.setDate(shipmentEvent.getDate().toGregorianCalendar().getTime());
                        event.setName(shipmentEvent.getServiceEvent().getDescription());
                        event.setLocation(shipmentEvent.getServiceArea().getDescription());
                        shippingEvents.add(event);
                    }
                }
                return shippingEvents;
            } else {
                ShipmentTrackingErrorResponse trackingErrorResponse = (ShipmentTrackingErrorResponse) dhlService.unmarshal(response, new Class[] { ShipmentTrackingErrorResponse.class });
                throw new Exception(trackingErrorResponse.getResponse().getStatus().getCondition().toString()); // TODO:
                                                                                                                // fix
                                                                                                                // this
            }

        } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (JAXBException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        return null;
    }

    private KnownTrackingRequest getTrackingRequest(String trackingNumber) throws DatatypeConfigurationException {
        KnownTrackingRequest trackingRequest = new KnownTrackingRequest();
        trackingRequest.setRequest(dhlService.getRequest());
        trackingRequest.setLevelOfDetails(LevelOfDetails.ALL_CHECK_POINTS);
        return trackingRequest;

    }

}
