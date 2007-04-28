package org.spbgu.pmpu.athynia.central;

import org.spbgu.pmpu.athynia.central.settings.Settings;
import org.spbgu.pmpu.athynia.central.settings.impl.XmlSettings;

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getData(Class<T> key) {
        return (T) map.get(key);
    }
}
