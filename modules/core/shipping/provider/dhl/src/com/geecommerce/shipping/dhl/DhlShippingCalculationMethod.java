package com.geecommerce.shipping.dhl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import com.dhl.DCTRequest;
import com.dhl.DCTResponse;
import com.dhl.dctrequestdatatypes.BkgDetailsType;
import com.dhl.dctrequestdatatypes.DCTFrom;
import com.dhl.dctrequestdatatypes.DCTTo;
import com.dhl.dctrequestdatatypes.PieceType;
import com.dhl.dctresponsedatatypes.QtdShpType;
import com.geecommerce.core.App;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.annotation.ShippingCalculationMethod;
import com.geecommerce.shipping.dhl.configuration.Key;
import com.geecommerce.shipping.model.ShippingItem;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.unit.converter.enums.LengthUnit;
import com.geecommerce.unit.converter.enums.MassUnit;
import com.geecommerce.unit.converter.service.LengthConverter;
import com.geecommerce.unit.converter.service.MassConverter;
import com.google.inject.Inject;

@ShippingCalculationMethod
public class DhlShippingCalculationMethod extends AbstractShippingCalculationMethod {
    @Inject
    protected App app;

    protected DhlService dhlService = new DhlService();

    @Override
    public boolean isEnabled() {
        return app.cpBool_(Key.ENABLED, false);
    }

    @Override
    public String getCode() {
        return "dhl";
    }

    private boolean couldCalculateShipment(ShippingPackage shippingData) {
        for (ShippingItem item : shippingData.getShippingItems()) {
            if (item.getWeight() == null || item.getWidth() == null || item.getHeight() == null
                || item.getDepth() == null)
                return false;
        }
        return true;
    }

    @Override
    public List<ShippingOption> getShipmentOptions(Object... data) {
        ShippingPackage shippingData = (ShippingPackage) data[0];

        if (!couldCalculateShipment(shippingData))
            return null;

        try {
            String request = getRequest(shippingData);
            HttpURLConnection connection = dhlService.createUrlConnection();
            dhlService.sendRequestToXmlPi(request, connection);
            String response = dhlService.readResponseFromXmlPi(connection);
            if (!dhlService.isErrorResponse(response, "res:ErrorResponse")) {
                DCTResponse dctResponse = (DCTResponse) dhlService.unmarshal(response,
                    new Class[] { DCTResponse.class });
                List<ShippingOption> options = new ArrayList<>();
                for (com.dhl.dctresponsedatatypes.BkgDetailsType bkgDetailsType : dctResponse.getGetQuoteResponse()
                    .getBkgDetails()) {
                    for (QtdShpType qtdShpType : bkgDetailsType.getQtdShp()) {
                        ShippingOption option = app.injectable(ShippingOption.class);
                        option.setCarrierCode(getCode());
                        option.setRate(qtdShpType.getShippingCharge().doubleValue());
                        option.setName(qtdShpType.getProductShortName());
                        option.setOptionCode(qtdShpType.getGlobalProductCode());// TODO:
                                                                                // may
                                                                                // be
                                                                                // we
                                                                                // will
                                                                                // need
                                                                                // to
                                                                                // use
                        // another
                        options.add(option);
                    }
                }

                return options;
            } else {
                // trackingErrorResponse = (ShipmentTrackingErrorResponse)
                // dhlService.unmarshal(response,
                // "com.dhl.ShipmentTrackingErrorResponse");
                // throw new
                // Exception(trackingErrorResponse.getResponse().getStatus().getCondition().toString());
                // //TODO: fix this

            }

        } catch (JAXBException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    private String getRequest(ShippingPackage shippingData) throws JAXBException, DatatypeConfigurationException {
        DCTRequest dctRequest = new DCTRequest();
        dctRequest.setGetQuote(getQuote(shippingData));
        return dhlService.marshal(dctRequest);
    }

    private DCTRequest.GetQuote getQuote(ShippingPackage shippingData) throws DatatypeConfigurationException {
        DCTRequest.GetQuote quote = new DCTRequest.GetQuote();
        quote.setFrom(getShippingAddress(shippingData));
        quote.setTo(getDeliveryAddress(shippingData));
        quote.setBkgDetails(getBkgDetails(shippingData));
        return quote;
    }

    private DCTTo getDeliveryAddress(ShippingPackage shippingData) {
        DCTTo to = new DCTTo();
        to.setCountryCode(shippingData.getShippingAddress().getCountry());
        to.setPostalcode(shippingData.getShippingAddress().getZip());
        // other are Optional
        return to;
    }

    private DCTFrom getShippingAddress(ShippingPackage shippingData) {
        DCTFrom from = new DCTFrom();
        from.setCountryCode(app.cpStr_(com.geecommerce.shipping.configuration.Key.DEPARTURE_COUNTRY));
        from.setPostalcode(app.cpStr_(com.geecommerce.shipping.configuration.Key.DEPARTURE_ZIP));
        // TODO: to shipping module settings
        return from;
    }

    private BkgDetailsType getBkgDetails(ShippingPackage shippingData) throws DatatypeConfigurationException {
        BkgDetailsType bkgDetailsType = new BkgDetailsType();
        if (usePiecesInfo()) {
            // We know information about all shipping items
            setPiecesInfo(bkgDetailsType, shippingData);
        } else {
            // We know information about package
            setPackageInfo(bkgDetailsType, shippingData);
        }
        return bkgDetailsType;
    }

    private boolean usePiecesInfo() {
        return true;
    }

    private XMLGregorianCalendar getShipmentReadyDate() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }

    private Duration getShippingDuration() throws DatatypeConfigurationException {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        return factory.newDurationDayTime(true, 1, 0, 0, 0); // in one day
    }

    private void setPiecesInfo(BkgDetailsType bkgDetails, ShippingPackage shippingData)
        throws DatatypeConfigurationException {
        BkgDetailsType.Pieces piecesContainer = new BkgDetailsType.Pieces();
        List<PieceType> pieces = piecesContainer.getPiece();

        bkgDetails.setDimensionUnit(app.cpStr_(Key.DIMENSION));
        bkgDetails.setWeightUnit(app.cpStr_(Key.WEIGHT));
        bkgDetails.setIsDutiable("N");

        // set some shipping data
        bkgDetails.setDate(getShipmentReadyDate());
        bkgDetails.setReadyTime(getShippingDuration());

        // set pieces info
        for (ShippingItem item : shippingData.getShippingItems()) {
            pieces.add(fromShippingItem(item));
        }
    }

    private LengthUnit getDhlLengthUnit() {
        String unit = app.cpStr_(Key.DIMENSION);
        if (unit.equals("CM"))
            return LengthUnit.CENTIMETER;
        if (unit.equals("IN"))
            return LengthUnit.INCH;
        return LengthUnit.UNSUPPORTED;
    }

    private MassUnit getDhlMassUnit() {
        String unit = app.cpStr_(Key.WEIGHT);
        if (unit.equals("KG"))
            return MassUnit.KILOGRAM;
        if (unit.equals("LB"))
            return MassUnit.POUND;
        return MassUnit.UNSUPPORTED;
    }

    private PieceType fromShippingItem(ShippingItem item) {
        PieceType piece = new PieceType();

        LengthConverter lengthConverter = app.service(LengthConverter.class);
        MassConverter massConverter = app.service(MassConverter.class);

        LengthUnit l_unit = getDhlLengthUnit();
        MassUnit m_unit = getDhlMassUnit();

        piece.setWidth(BigDecimal.valueOf(massConverter.convert(item.getWeight(), m_unit)));
        piece.setWidth(BigDecimal.valueOf(lengthConverter.convert(item.getWidth(), l_unit)));
        piece.setHeight(BigDecimal.valueOf(lengthConverter.convert(item.getHeight(), l_unit)));
        piece.setDepth(BigDecimal.valueOf(lengthConverter.convert(item.getDepth(), l_unit)));

        piece.setPackageTypeCode("BOX"); // use default value but have some
                                         // options

        return piece;
    }

    private void setPackageInfo(BkgDetailsType bkgDetails, ShippingPackage shippingData) {
        // TODO: implement if needed
    }
}
