package org.spbgu.pmpu.athynia.worker.resource;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.StringComparator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.util.Properties;

/**
 * User: A.Selivanov
 * Date: 26.05.2007
 */
public class BTreeResourceManager implements ResourceManager {
    private static final Logger LOG = Logger.getLogger(BTreeResourceManager.class);

    private RecordManager recordManager;
    private BTree tree;
    private Properties properties = new Properties();
    public static final String DEFAULT_DATABASE_FILE_NAME = "TEST";
    public static final String BTREE_NAME = "test";//todo: multiLoad

    public BTreeResourceManager() {
        this(DEFAULT_DATABASE_FILE_NAME);
    }

    public BTreeResourceManager(String dataBaseFileName){
        PropertyConfigurator.configure("log4j.properties");
         try {
            recordManager = RecordManagerFactory.createRecordManager(dataBaseFileName, properties);
            long recid = recordManager.getNamedObject(BTREE_NAME);
            if (recid != 0) {
                tree = BTree.load(recordManager, recid);
                LOG.info("Reloaded existing BTree with " + tree.size() + " items.");
            } else {
                tree = BTree.createInstance(recordManager, new StringComparator(), null, new JoinPartSerializer());
                recordManager.setNamedObject(BTREE_NAME, tree.getRecid());
                LOG.info("Created a new empty BTree");
            }
        } catch (Exception e) {
            LOG.error("Error on creating BTree ", e);
        }
    }

    public void commit() {
        try {
            recordManager.commit();
        } catch (Exception e) {
            LOG.error("Error while commiting Resorce BTree", e);
        }
    }

    public void abort() {
        try {
            recordManager.rollback();
        } catch (Exception e) {
            LOG.error("Error on abort Resorce BTree", e);
        }
    }

    public void write(String key, String value) {
        write(key, value, 0, 0, CommunicationConstants.TIMEOUT_UNTIL_DATA_DROP);
    }

    public void write(String key, String value, int currentPartNumber, int wholeNumbers, long timeoutUntilDrop) {
        try {
            JoinPart part = new JoinPartImpl(key, value, currentPartNumber, wholeNumbers);
            tree.insert(key, part, true);
        } catch (Exception e) {
            LOG.error("Error while writing to Resorce BTree", e);
        }
    }

    public JoinPart remove(String key) {
        try {
            return (JoinPart) tree.remove(key);
        } catch (Exception e) {
            LOG.error("Error while deleting from Resorce BTree", e);
        }
        return null;
    }

    public int getSize() {
        return tree.size();
    }

    public JoinPart search(String key) {
        try {
            return (JoinPart) tree.find(key);
        } catch (Exception e) {
            LOG.error("Error while searching in Resorce BTree", e);
        }
        return null;
    }

}
