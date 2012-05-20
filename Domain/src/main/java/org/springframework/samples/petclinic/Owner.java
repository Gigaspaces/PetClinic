package org.springframework.samples.petclinic;

import com.gigaspaces.annotation.pojo.SpaceExclude;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.core.style.ToStringCreator;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsa.GridServiceAgent;
import org.openspaces.admin.gsa.GridServiceContainerOptions;

import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name="owners")
public class Owner extends Person implements Serializable {

	private String address;
	private String city;
	private String telephone;
    @Transient
    private Set<Pet> pets;

    public Owner() {}

    public Owner(Integer id, String firstName, String lastName, String address, String city,
                 String telephone, Pet... pets) {
        super(id, firstName, lastName);
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        for (Pet pet : pets) {
            addPet(pet);    
        }

    }

    public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

    public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

    protected void setPetsInternal(Set<Pet> pets) {
		this.pets = pets;
	}

    @SpaceExclude
    protected Set<Pet> getPetsInternal() {
		if (this.pets == null) {
			this.pets = new HashSet<Pet>();
		}
		return this.pets;
	}

    @SpaceExclude
    public List<Pet> getPets() {
		List<Pet> sortedPets = new ArrayList<Pet>(getPetsInternal());
		PropertyComparator.sort(sortedPets, new MutableSortDefinition("name", true, true));
		return Collections.unmodifiableList(sortedPets);
	}

	public void addPet(Pet pet) {
		getPetsInternal().add(pet);        
        pet.setOwner(this);
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 *
	 * @param name to test
	 * @return true if pet name is already in use
	 */
	public Pet getPet(String name) {
		return getPet(name, false);
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 *
	 * @param name to test
	 * @return true if pet name is already in use
	 */
	public Pet getPet(String name, boolean ignoreNew) {
		name = name.toLowerCase();
		for (Pet pet : getPetsInternal()) {
			if (!ignoreNew || !pet.isNew()) {
				String compName = pet.getName();
				compName = compName.toLowerCase();
				if (compName.equals(name)) {
					return pet;
				}
			}
		}
		return null;
	}

    @Override
	public String toString() {
		return new ToStringCreator(this)

		.append("id", this.getId())

		.append("new", this.isNew())

		.append("lastName", this.getLastName())

		.append("firstName", this.getFirstName())

		.append("address", this.address)

		.append("city", this.city)

		.append("telephone", this.telephone)

		.toString();
	}
}
