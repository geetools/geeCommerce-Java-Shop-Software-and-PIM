//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.20 at 05:10:42 PM MSK 
//

package com.dhl.dctrequestdatatypes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QtdShpType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="QtdShpType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GlobalProductCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[A-Z0-9]+"/>
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LocalProductCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="QtdShpExChrg" type="{http://www.dhl.com/DCTRequestdatatypes}QtdShpExChrgType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QtdShpType", propOrder = { "globalProductCode", "localProductCode", "qtdShpExChrg" })
public class QtdShpType {

    @XmlElement(name = "GlobalProductCode")
    protected String globalProductCode;
    @XmlElement(name = "LocalProductCode")
    protected String localProductCode;
    @XmlElement(name = "QtdShpExChrg")
    protected List<QtdShpExChrgType> qtdShpExChrg;

    /**
     * Gets the value of the globalProductCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getGlobalProductCode() {
        return globalProductCode;
    }

    /**
     * Sets the value of the globalProductCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setGlobalProductCode(String value) {
        this.globalProductCode = value;
    }

    /**
     * Gets the value of the localProductCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getLocalProductCode() {
        return localProductCode;
    }

    /**
     * Sets the value of the localProductCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setLocalProductCode(String value) {
        this.localProductCode = value;
    }

    /**
     * Gets the value of the qtdShpExChrg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the qtdShpExChrg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getQtdShpExChrg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QtdShpExChrgType }
     * 
     * 
     */
    public List<QtdShpExChrgType> getQtdShpExChrg() {
        if (qtdShpExChrg == null) {
            qtdShpExChrg = new ArrayList<QtdShpExChrgType>();
        }
        return this.qtdShpExChrg;
    }

}
