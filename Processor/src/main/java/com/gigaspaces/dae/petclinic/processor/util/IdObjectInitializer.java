package com.gigaspaces.dae.petclinic.processor.util;

import com.gigaspaces.dae.domain.id.IdCounterEntry;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.openspaces.core.space.mode.PostPrimary;

import javax.annotation.Resource;

/**
 * User: uri
 * Date: Sep 12, 2008
 * Time: 2:46:07 PM
 * GigaSpaces Technologies
 */
public class IdObjectInitializer {

    @Resource
    private GigaSpace gigaSpace;
    
    @ClusterInfoContext
    private ClusterInfo clusterInfo;
    private int idRangeSize = 100;

    /**
     * Sets the range size,
     * @param idRangeSize
     */
    public void setIdRangeSize(int idRangeSize) {
        this.idRangeSize = idRangeSize;
    }

    @PostPrimary
    public void init() {
        if (shouldWriteIdObjectToSpace())   {
            IdCounterEntry existingEntry = gigaSpace.read(new IdCounterEntry());
            if (existingEntry == null) {
                gigaSpace.write(new IdCounterEntry(100, idRangeSize));
            }
        }
    }

    //return true if we don't have a clusterInfo/instance id, or if this the first
    //primary instance in the cluster
    private boolean shouldWriteIdObjectToSpace() {
        if  (clusterInfo == null)
            return true;
        if (clusterInfo.getInstanceId() == null)
            return true;
        if (clusterInfo.getInstanceId() == 1)
            return true;
        return false;
    }
}
