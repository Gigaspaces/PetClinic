package com.gigaspaces.dae.domain.id;

import com.gigaspaces.annotation.pojo.*;

import javax.persistence.*;

/**
 * User: uri
 * Date: Sep 12, 2008
 * Time: 3:02:09 PM
 * GigaSpaces Technologies
 */
@Entity
@Table(name = "id_counter")
public class IdCounterEntry {

    private Integer currentId;
    private Integer idRangeSize;


    public IdCounterEntry() {}

    public IdCounterEntry(int currentId,int idRangeSize) {
        this.idRangeSize = idRangeSize;
        this.currentId = currentId; 
    }

    public Integer getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
    }

    public Integer getIdRangeSize() {
        return idRangeSize;
    }

    public void setIdRangeSize(Integer idRangeSize) {
        this.idRangeSize = idRangeSize;
    }

    @SpaceExclude
    @Transient
    public int[] getIdRange() {
        int endId = currentId + idRangeSize;
        int[] range = new int[]{currentId, endId-1};
        currentId += idRangeSize;
        return range;

    }

    @Id
    @SpaceId
    @SpaceRouting
    protected Integer getRouting() {
        return 0;
    }                                          
    protected void setRouting(Integer routing) {}

}
