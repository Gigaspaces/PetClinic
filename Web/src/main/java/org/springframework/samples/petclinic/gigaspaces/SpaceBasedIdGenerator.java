package org.springframework.samples.petclinic.gigaspaces;

import com.gigaspaces.dae.domain.id.IdCounterEntry;
import com.gigaspaces.dae.domain.id.IdGenerator;
import com.j_spaces.core.client.ReadModifiers;
import com.j_spaces.core.client.UpdateModifiers;
import net.jini.core.lease.Lease;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * User: uri
 * Date: Sep 7, 2008
 * Time: 5:06:34 PM
 * GigaSpaces Technologies
 */
public class SpaceBasedIdGenerator implements IdGenerator {

    private int currentId = 0;
    @Resource
    private GigaSpace gigaSpace;
    private int idLimit = -1;
    private final static IdCounterEntry template = new IdCounterEntry();

	@Transactional(propagation=Propagation.REQUIRES_NEW)
    public synchronized Integer generateId() {
        if (currentId < 0 || currentId > idLimit) {
            getNextIdBatchFromSpace();
        }
        return currentId++;
    }

    
    private void getNextIdBatchFromSpace() {
        IdCounterEntry idCounterEntry = gigaSpace.read(template, 5000, ReadModifiers.EXCLUSIVE_READ_LOCK);
        if (idCounterEntry == null) {
            throw new RuntimeException("Could not get ID object from Space");
        }
        int[] range = idCounterEntry.getIdRange();
        currentId = range[0];
        idLimit = range[1];
        gigaSpace.write(idCounterEntry, Lease.FOREVER, 5000, UpdateModifiers.UPDATE_ONLY);
    }

    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }


    public static void main(String[] args) throws Exception {
        GigaSpace gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("jini://*/*/petclinic").space()).gigaSpace();
        SpaceBasedIdGenerator generator = new SpaceBasedIdGenerator();
        generator.setGigaSpace(gigaSpace);
        for (int i=0; i<110;i++) {
            int id = generator.generateId();
            if (i != id) {
                System.out.println("Unexpected ID, expected " + i +" and got " + id);
            }
        }
    }


}




