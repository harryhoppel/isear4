package org.spbgu.pmpu.athynia.worker;

import org.spbgu.pmpu.athynia.worker.index.Index;
import org.spbgu.pmpu.athynia.worker.index.impl.MemoryIndex;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.common.settings.impl.XmlSettings;

import java.util.Map;
import java.util.HashMap;

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

            final Index index = new MemoryIndex();
            map.put(Index.class, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getData(Class<T> key) {
        return (T) map.get(key);
    }
}
