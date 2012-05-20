package org.springframework.samples.petclinic;

import com.gigaspaces.annotation.pojo.SpaceRouting;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name="vets")
public class Vet extends Person implements Serializable {

    public Vet() {}

    @Override
    public void setId(Integer id) {
        super.setId(id);
    }

    @Override
    @SpaceRouting
    public Integer getId() {
        return super.getId();
    }

//    @Type(type = "serializable")
    @ManyToMany(targetEntity = Specialty.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name="vet_specialties",
               joinColumns=@JoinColumn(name="vet_id"),
               inverseJoinColumns=@JoinColumn(name="specialty_id"))
    private Set<Specialty> specialties;

    public Vet(Integer id, String firstName, String lastName, Specialty... specialities) {
        super(id, firstName, lastName);
        for (Specialty speciality : specialities) {
            addSpecialty(speciality);
        }
    }

	protected void setSpecialtiesInternal(Set<Specialty> specialties) {
		this.specialties = specialties;
	}

	protected Set<Specialty> getSpecialtiesInternal() {
		return this.specialties;
	}

    public List<Specialty> getSpecialties() {
        if (getSpecialtiesInternal() == null) {
            return Collections.emptyList();
        }
        List<Specialty> sortedSpecs = new ArrayList<Specialty>(getSpecialtiesInternal());
		PropertyComparator.sort(sortedSpecs, new MutableSortDefinition("name", true, true));
		return Collections.unmodifiableList(sortedSpecs);
	}

    public int getNrOfSpecialties() {
		return getSpecialtiesInternal() != null?getSpecialties().size():0;
	}

	public void addSpecialty(Specialty specialty) {
        if (getSpecialtiesInternal() == null) {
			setSpecialtiesInternal(new HashSet<Specialty>());
		}
        getSpecialtiesInternal().add(specialty);
	}

}
