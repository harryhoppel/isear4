package org.spbgu.pmpu.athynia.worker;

import org.spbgu.pmpu.athynia.worker.resource.BTreeResourceManager;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.common.settings.impl.XmlSettings;
import org.spbgu.pmpu.athynia.worker.network.CentralConnectionManager;
import org.spbgu.pmpu.athynia.worker.network.impl.CentralConnectionManagerImpl;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private Logger LOG = Logger.getLogger(DataManager.class);
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

            final ResourceManager resourceManager = new BTreeResourceManager(settings.childSettings("resource-manager").getValue("database_file_name", BTreeResourceManager.DEFAULT_DATABASE_FILE_NAME));
            map.put(ResourceManager.class, resourceManager);
        } catch (Exception e) {
            LOG.error("error initilization datamanager", e);
        }
    }

    public <T> T getData(Class<T> key) {
        return (T) map.get(key);
    }
}
