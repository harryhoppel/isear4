package org.spbgu.pmpu.athynia.worker.index;

import java.io.Serializable;
import java.util.Collection;

/**
 * Author: Selivanov
 * Date: 04.03.2007
 * Time: 11:30:21
 */
public interface Node extends Comparable, Serializable {
    String getText();

    Collection<Node> getParent();

    Collection<Node> getChilds();
}
