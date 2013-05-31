
package org.fosstrak.ale.xsd.ale.epcglobal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.fosstrak.ale.xsd.epcglobal.Document;


/**
 * <p>Java class for TMSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TMSpec">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:xsd:1}Document">
 *       &lt;sequence>
 *         &lt;element name="baseExtension" type="{urn:epcglobal:ale:xsd:1}TMSpecExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TMSpec", propOrder = {
    "baseExtension"
})
public abstract class TMSpec
    extends Document
{

    protected TMSpecExtension baseExtension;

    /**
     * Gets the value of the baseExtension property.
     * 
     * @return
     *     possible object is
     *     {@link TMSpecExtension }
     *     
     */
    public TMSpecExtension getBaseExtension() {
        return baseExtension;
    }

    /**
     * Sets the value of the baseExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TMSpecExtension }
     *     
     */
    public void setBaseExtension(TMSpecExtension value) {
        this.baseExtension = value;
    }

}
