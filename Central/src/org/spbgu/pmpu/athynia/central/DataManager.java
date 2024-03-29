package org.spbgu.pmpu.athynia.central;

import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.impl.WorkersManagerImpl;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.common.settings.impl.XmlSettings;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    Logger LOG = Logger.getLogger(DataManager.class);
    private static DataManager ourInstance = new DataManager();
    private Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        try {
            final Settings settings = XmlSettings.load("settings.xml");
            map.put(Settings.class, settings);

            final WorkersManager workersManager = WorkersManagerImpl.getInstance();
            map.put(WorkersManager.class, workersManager);
        } catch (Exception e) {
            LOG.error("error initilization datamanager", e);
        }
    }

    public <T> T getData(Class<T> key) {
        return (T) map.get(key);
    }
}
