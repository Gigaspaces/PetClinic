package org.springframework.samples.petclinic.gigaspaces;

import com.gigaspaces.dae.domain.id.IdGenerator;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.springframework.samples.petclinic.*;
import org.springframework.stereotype.Repository;
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
public class GigaSpacesClinic implements Clinic {

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
        Vet[] results = gigaSpace.readMultiple(new SQLQuery<Vet>(Vet.class,  ""), Integer.MAX_VALUE);
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
        SQLQuery<Owner> query = new SQLQuery(Owner.class, "lastName like ?", "%"+lastName+"%");
        Owner[] results = gigaSpace.readMultiple(query, Integer.MAX_VALUE);
        if (results == null || results.length == 0) {
            return Collections.emptyList();
        }
        for (Owner owner : results) {
            loadPetsForOwner(owner);
        }
        return Arrays.asList(results);
    }

    @Transactional(readOnly = true)
    public Owner loadOwner(int id) {
        Owner owner = gigaSpace.readById(Owner.class, id);
        loadPetsForOwner(owner);
        return owner;
    }

    private void loadPetsForOwner(Owner owner) {
        Integer ownerId = owner.getId();
        Pet template = new Pet();
        template.setOwnerId(ownerId);
        Pet[] pets = gigaSpace.readMultiple(template, Integer.MAX_VALUE);
        if (pets != null) {
            for (Pet pet : pets) {
                owner.addPet(pet);
                loadVisitsForPet(pet);
            }
        }
    }


    public Pet loadPet(int id) {
        Pet pet = gigaSpace.readById(Pet.class, id);
        Integer ownerId = pet.getOwnerId();
        if (ownerId != null) {
            Owner owner = gigaSpace.readById(Owner.class, ownerId);
            loadPetsForOwner(owner);
            owner.addPet(pet);
        }
        loadVisitsForPet(pet);
        return pet;
    }

    private void loadVisitsForPet(Pet pet) {
        Visit[] visits = gigaSpace.readMultiple(new SQLQuery<Visit>(Visit.class, "petId=? AND petOwnerId=?",
                                                                    pet.getId(), pet.getOwnerId()), Integer.MAX_VALUE);
        if (visits != null) {
            for (Visit visit : visits) {
                pet.addVisit(visit);
            }
        }
    }

    @Transactional
    public void storeOwner(Owner owner) {
        List<Pet> pets = owner.getPets();
        if (!pets.isEmpty()) {
            for (Pet pet : pets) {
                storePetInternal(pet);
            }
        }
        writeToSpaceWithId(owner);
    }

    @Transactional
    public void storePet(Pet pet) {
        Owner owner = pet.getOwner();
        if (owner != null) {
            writeToSpaceWithId(owner);
        }
        storePetInternal(pet);
    }

    private void storePetInternal(Pet pet) {
        List<Visit> visits = pet.getVisits();
        for (Visit visit : visits) {
            storeVisit(visit);
        }
        writeToSpaceWithId(pet);
    }

    @Transactional
    public void storeVisit(Visit visit) {
        writeToSpaceWithId(visit);
    }

    private void writeToSpaceWithId(BaseEntity entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.generateId());
        }
        gigaSpace.write(entity);
    }


}

