package org.spbgu.pmpu.athynia.worker;

import org.spbgu.pmpu.athynia.common.LocalResourceManager;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.common.settings.impl.XmlSettings;
import org.spbgu.pmpu.athynia.worker.network.CentralConnectionManager;
import org.spbgu.pmpu.athynia.worker.network.impl.CentralConnectionManagerImpl;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static DataManager ourInstance = new DataManager();
    private Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        try {
            final Settings settings = XmlSettings.load("settings.xml");
            map.put(Settings.class, settings);

            final CentralConnectionManager centralConnectionManager = new CentralConnectionManagerImpl();
            map.put(CentralConnectionManager.class, centralConnectionManager);

            final LocalResourceManager localResourceManager = new LocalResourceManager();
            map.put(LocalResourceManager.class, localResourceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getData(Class<T> key) {
        return (T) map.get(key);
    }
}
