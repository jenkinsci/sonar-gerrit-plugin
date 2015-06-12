package com.aquarellian.genar.filter;

import com.aquarellian.genar.filter.model.Filter;
import com.aquarellian.genar.filter.model.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 12.06.2015 17:08
 * <p/>
 * $Id$
 */
public class FilterParser {
    public Filter parseFilter(String source) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        URL url = this.getClass().getClassLoader().getResource(source);

        return (Filter) unmarshaller.unmarshal(url);
    }
}
