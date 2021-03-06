//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.20 at 05:11:11 PM MSK 
//

package com.dhl.dctresponsedatatypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QtdSExtrChrgInAdCurType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="QtdSExtrChrgInAdCurType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChargeValue" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;totalDigits value="18"/>
 *               &lt;fractionDigits value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChargeExchangeRate" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;totalDigits value="18"/>
 *               &lt;fractionDigits value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChargeTaxAmount" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;totalDigits value="18"/>
 *               &lt;fractionDigits value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CurrencyCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CurrencyRoleTypeCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChargeTaxAmountDet" type="{http://www.dhl.com/DCTResponsedatatypes}ChargeTaxAmountDetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QtdSExtrChrgInAdCurType", propOrder = { "chargeValue", "chargeExchangeRate", "chargeTaxAmount",
    "currencyCode", "currencyRoleTypeCode", "chargeTaxAmountDet" })
public class QtdSExtrChrgInAdCurType {

    @XmlElement(name = "ChargeValue")
    protected BigDecimal chargeValue;
    @XmlElement(name = "ChargeExchangeRate")
    protected BigDecimal chargeExchangeRate;
    @XmlElement(name = "ChargeTaxAmount")
    protected BigDecimal chargeTaxAmount;
    @XmlElement(name = "CurrencyCode")
    protected String currencyCode;
    @XmlElement(name = "CurrencyRoleTypeCode")
    protected String currencyRoleTypeCode;
    @XmlElement(name = "ChargeTaxAmountDet")
    protected List<ChargeTaxAmountDetType> chargeTaxAmountDet;

    /**
     * Gets the value of the chargeValue property.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    /**
     * Sets the value of the chargeValue property.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setChargeValue(BigDecimal value) {
        this.chargeValue = value;
    }

    /**
     * Gets the value of the chargeExchangeRate property.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getChargeExchangeRate() {
        return chargeExchangeRate;
    }

    /**
     * Sets the value of the chargeExchangeRate property.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setChargeExchangeRate(BigDecimal value) {
        this.chargeExchangeRate = value;
    }

    /**
     * Gets the value of the chargeTaxAmount property.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getChargeTaxAmount() {
        return chargeTaxAmount;
    }

    /**
     * Sets the value of the chargeTaxAmount property.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setChargeTaxAmount(BigDecimal value) {
        this.chargeTaxAmount = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the currencyRoleTypeCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCurrencyRoleTypeCode() {
        return currencyRoleTypeCode;
    }

    /**
     * Sets the value of the currencyRoleTypeCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCurrencyRoleTypeCode(String value) {
        this.currencyRoleTypeCode = value;
    }

    /**
     * Gets the value of the chargeTaxAmountDet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the chargeTaxAmountDet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getChargeTaxAmountDet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChargeTaxAmountDetType }
     * 
     * 
     */
    public List<ChargeTaxAmountDetType> getChargeTaxAmountDet() {
        if (chargeTaxAmountDet == null) {
            chargeTaxAmountDet = new ArrayList<ChargeTaxAmountDetType>();
        }
        return this.chargeTaxAmountDet;
    }

}
