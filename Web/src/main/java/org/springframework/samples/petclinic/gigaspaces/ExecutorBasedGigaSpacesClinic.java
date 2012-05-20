package org.springframework.samples.petclinic.gigaspaces;

import com.gigaspaces.async.AsyncResult;
import com.gigaspaces.dae.domain.id.IdGenerator;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.DistributedTask;
import org.openspaces.core.executor.Task;
import org.openspaces.core.executor.TaskGigaSpace;
import org.springframework.samples.petclinic.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * GigaSpaces implementation of the Clinic interface.
 *
 * @author Uri Cohen
 * @since 26.8.08
 */
@Repository
public class ExecutorBasedGigaSpacesClinic implements Clinic {

    @Resource
    private GigaSpace gigaSpace;

    @Resource
    IdGenerator idGenerator;

    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @SuppressWarnings("unchecked")
    public Collection<Vet> getVets() {
        Vet[] results = gigaSpace.readMultiple(new Vet(), Integer.MAX_VALUE);
        if (results == null || results.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(results);
    }

    @SuppressWarnings("unchecked")
    public Collection<PetType> getPetTypes() {
        Pet[] results = gigaSpace.readMultiple(new Pet(), Integer.MAX_VALUE);
        if (results == null || results.length == 0) {
            return Collections.emptyList();
        }
        Collection<PetType> petTypeSet = new HashSet<PetType>();
        for (Pet pet : results) {
            petTypeSet.add(pet.getType());
        }
        List<PetType> petTypeList = new ArrayList<PetType>(petTypeSet);
        Collections.sort(petTypeList, new Comparator<PetType>() {
            public int compare(PetType o1, PetType o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return petTypeList;
    }

    @SuppressWarnings("unchecked")
    public Collection<Owner> findOwners(String lastName) {
        try {
            return gigaSpace.execute(new FindOwnersTask(lastName)).get();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to find owners", e);
        }
    }

    public Owner loadOwner(int id) {
        try {
            return gigaSpace.execute(new LoadOwnerTask(id), id).get();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to find owners", e);
        }
    }

    private static void loadPetsForOwner(GigaSpace gigaSpace, Owner owner) {
        Integer ownerId = owner.getId();
        Pet template = new Pet();
        template.setOwnerId(ownerId);
        Pet[] pets = gigaSpace.readMultiple(template, Integer.MAX_VALUE);
        if (pets != null) {
            for (Pet pet : pets) {
                owner.addPet(pet);
                loadVisitsForPet(gigaSpace, pet);
            }
        }
    }

    public Pet loadPet(int id) {
        try {
            return gigaSpace.execute(new LoadPetTask(id)).get();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to find owners", e);
        }
    }

    private static void loadVisitsForPet(GigaSpace gigaSpace, Pet pet) {
        Visit[] visits = gigaSpace.readMultiple(new SQLQuery<Visit>(Visit.class, "petId=? AND petOwnerId=?",
                pet.getId(), pet.getOwnerId()), Integer.MAX_VALUE);
        if (visits != null) {
            for (Visit visit : visits) {
                pet.addVisit(visit);
            }
        }
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void storeOwner(Owner owner) {
        List<Pet> pets = owner.getPets();
        if (!pets.isEmpty()) {
            for (Pet pet : pets) {
                storePetInternal(pet);
            }
        }
        writeToSpaceWithId(owner);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void storePet(Pet pet) {
//        Owner owner = pet.getOwner();
//        if (owner != null) {
//            writeToSpaceWithId(owner);
//        }
        storePetInternal(pet);
    }

    private void storePetInternal(Pet pet) {
//        List<Visit> visits = pet.getVisits();
//        for (Visit visit : visits) {
//            storeVisit(visit);
//        }
        writeToSpaceWithId(pet);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void storeVisit(Visit visit) {
        writeToSpaceWithId(visit);
    }

    private void writeToSpaceWithId(BaseEntity entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.generateId());
        }
        gigaSpace.write(entity);
    }


    private static class FindOwnersTask implements DistributedTask<Owner[], Collection<Owner>> {
        @TaskGigaSpace
        private transient GigaSpace gigaSpace;
        private String ownerLastName;

        private FindOwnersTask(String ownerLastName) {
            this.ownerLastName = ownerLastName;
        }

        public Owner[] execute() throws Exception {
            SQLQuery<Owner> query = new SQLQuery<Owner>(Owner.class, "lastName like ?", "%" + ownerLastName + "%");
            Owner[] results = gigaSpace.readMultiple(query, Integer.MAX_VALUE);
            if (results != null) {
                for (Owner owner : results) {
                    loadPetsForOwner(gigaSpace, owner);
                }
            }
            return results;
        }

        public Collection<Owner> reduce(List<AsyncResult<Owner[]>> asyncResults) throws Exception {
            Set<Owner> owners = new HashSet<Owner>();
            for (AsyncResult<Owner[]> asyncResult : asyncResults) {
                if (asyncResult.getException() != null) {
                    throw asyncResult.getException();
                }
                Owner[] result = asyncResult.getResult();
                if (result != null && result.length > 0) {
                    owners.addAll(Arrays.asList(result));
                }
            }
            return owners;
        }
    }

    private static class LoadOwnerTask implements Task<Owner> {
        @TaskGigaSpace
        private transient GigaSpace gigaSpace;
        private int ownerId;

        private LoadOwnerTask(int ownerId) {
            this.ownerId = ownerId;
        }

        public Owner execute() throws Exception {
            Owner owner = gigaSpace.readById(Owner.class, ownerId);
            loadPetsForOwner(gigaSpace, owner);
            return owner;
        }
    }

    private static class LoadPetTask implements DistributedTask<Pet,Pet> {
        @TaskGigaSpace
        private transient GigaSpace gigaSpace;
        private int petId;

        private LoadPetTask(int petId) {
            this.petId = petId;
        }

        public Pet execute() throws Exception {
            Pet pet = gigaSpace.readById(Pet.class, petId);
            if (pet != null) {
                Integer ownerId = pet.getOwnerId();
                if (ownerId != null) {
                    Owner owner = gigaSpace.readById(Owner.class, ownerId);
                    loadPetsForOwner(gigaSpace, owner);
                    owner.addPet(pet);
                }
                loadVisitsForPet(gigaSpace, pet);
            }
            return pet;
        }

        public Pet reduce(List<AsyncResult<Pet>> asyncResults) throws Exception {
            for (AsyncResult<Pet> asyncResult : asyncResults) {
                if (asyncResult.getException() != null) {
                    throw asyncResult.getException();
                }
                Pet result = asyncResult.getResult();
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

    }

}