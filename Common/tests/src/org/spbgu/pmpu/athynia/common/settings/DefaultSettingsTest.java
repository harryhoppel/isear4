package org.spbgu.pmpu.athynia.common.settings;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.common.settings.impl.DefaultSettings;

import java.io.File;

/**
 * Author: Pisar
 * Date: 29.03.2007
 * Time: 13:45:26
 */

public class DefaultSettingsTest extends TestCase {
    private static final String DATA_PATH = "tests/data/settings/";

    public void testDefaultSettiings() throws Exception {
        final File settingsFile = new File(DATA_PATH + "test-settings.xml");
        Settings settings = DefaultSettings.load(settingsFile.toURI().toURL().openStream());
        Settings crawlerSettings = settings.childSettings("crawlers");
        Settings ftpSettings = crawlerSettings.childSettings("ftp-crawler");
        Settings sambaSettings = crawlerSettings.childSettings("smb-crawler");
        assertEquals(10, sambaSettings.getIntValue("numThreads"));
        assertEquals(10, ftpSettings.getIntValue("numThreads"));
        assertEquals("null", ftpSettings.getValue("nothing", "null"));
    }
}

