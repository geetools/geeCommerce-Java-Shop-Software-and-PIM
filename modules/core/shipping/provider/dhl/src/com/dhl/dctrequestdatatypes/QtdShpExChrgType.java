//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.20 at 05:10:42 PM MSK 
//

package com.dhl.dctrequestdatatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QtdShpExChrgType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="QtdShpExChrgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpecialServiceType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LocalSpecialServiceType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QtdShpExChrgType", propOrder = { "specialServiceType", "localSpecialServiceType" })
public class QtdShpExChrgType {

    @XmlElement(name = "SpecialServiceType")
    protected String specialServiceType;
    @XmlElement(name = "LocalSpecialServiceType")
    protected String localSpecialServiceType;

    /**
     * Gets the value of the specialServiceType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSpecialServiceType() {
        return specialServiceType;
    }

    /**
     * Sets the value of the specialServiceType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSpecialServiceType(String value) {
        this.specialServiceType = value;
    }

    /**
     * Gets the value of the localSpecialServiceType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getLocalSpecialServiceType() {
        return localSpecialServiceType;
    }

    /**
     * Sets the value of the localSpecialServiceType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setLocalSpecialServiceType(String value) {
        this.localSpecialServiceType = value;
    }

}
