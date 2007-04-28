package org.spbgu.pmpu.athynia.worker.index.impl;

import org.spbgu.pmpu.athynia.worker.index.Index;
import org.spbgu.pmpu.athynia.worker.index.data.DocumentUpdate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: Selivanov
 * Date: 03.03.2007
 * Time: 21:57:20
 */
public class MemoryIndex implements Index {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final Map<String, Set<String>> indexMap = new HashMap<String, Set<String>>();

    public void write(DocumentUpdate... docUpdates) {
        for (DocumentUpdate docUpdate : docUpdates) {
            String[] uniqueWords = docUpdate.getUniqueWords();
            for (String uniqueWord : uniqueWords) {
                Set<String> listOfAddresses = indexMap.get(uniqueWord);
                boolean wordAlreadyInIndex = false;
                if (listOfAddresses == null) {
                    listOfAddresses = new HashSet<String>();
                } else {
                    wordAlreadyInIndex = true;
                }
                listOfAddresses.add(docUpdate.getAddress());
                if (!wordAlreadyInIndex) {
                    indexMap.put(uniqueWord, listOfAddresses);
                }
            }
        }
    }

    public String[] read(String keyWord) {
        Set<String> result = indexMap.get(keyWord);
        if (result == null) {
            return EMPTY_STRING_ARRAY;
        } else {
            return result.toArray(EMPTY_STRING_ARRAY);
        }
    }
}
