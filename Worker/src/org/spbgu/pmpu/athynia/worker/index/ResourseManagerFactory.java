package org.spbgu.pmpu.athynia.worker.index;

import java.io.IOException;
import java.util.Properties;

/**
 * User: A.Selivanov
 * Date: 19.05.2007
 */
public class ResourseManagerFactory {
    public static ResourseManager createResourseManager(String name) throws IOException {
        return createResourseManager(name, new Properties());
    }

    public static ResourseManager createResourseManager(String name, Properties options) throws IOException {
        {
            String provider;
            Class clazz;
            ResourseManagerProvider factory;

            provider = options.getProperty(ResourseManagerOptions.PROVIDER_FACTORY,
                "org.spbgu.pmpu.athynia.worker.index.resourse.Provider");

            try {
                clazz = Class.forName(provider);
                factory = (ResourseManagerProvider) clazz.newInstance();
            } catch (Exception except) {
                throw new IllegalArgumentException("Invalid record manager provider: "
                    + provider
                    + "\n[" + except.getClass().getName()
                    + ": " + except.getMessage()
                    + "]");
            }
            return factory.createResourseManager(name, options);
        }
    }
}
