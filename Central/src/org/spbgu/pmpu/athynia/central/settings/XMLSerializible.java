package org.spbgu.pmpu.athynia.central.settings;

import org.jdom.Element;

/**
 * User: Pisar
 * Date: 09.11.2005
 * Time: 17:54:33
 */
public interface XMLSerializible {
    void load(Element e) throws IllegalConfigValueException;

    void write(Element e) throws IllegalConfigValueException;
}
