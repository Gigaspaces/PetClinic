package org.springframework.samples.petclinic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.annotation.pojo.SpaceExclude;

import javax.persistence.*;

/**
 * Simple JavaBean business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name="pets")
public class Pet extends NamedEntity implements Serializable {

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PetType type;

    @Transient
    private Owner owner;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Transient
    private Set<Visit> visits;

    public Pet() {}

    public Pet(Integer id, String name, Date birthDate, PetType type, Owner owner, Visit... visits) {
        super(id, name);
        this.birthDate = birthDate;
        setType(type);
        setOwner(owner);
        if (visits != null) {
            for (Visit visit : visits) {
                addVisit(visit);
            }
        }
    }

    public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

    public void setType(PetType type) {
		this.type = type;        
    }

	public PetType getType() {
		return this.type;
	}

    public void setOwner(Owner owner) {
		this.owner = owner;
        setOwnerId(owner.getId());
    }

	@SpaceExclude
    public Owner getOwner() {
		return this.owner;
	}

	protected void setVisitsInternal(Set<Visit> visits) {
		this.visits = visits;
	}

    @SpaceExclude
    protected Set<Visit> getVisitsInternal() {
		if (this.visits == null) {
			this.visits = new HashSet<Visit>();
		}
		return this.visits;
	}

    @SpaceExclude
    public List<Visit> getVisits() {
		List<Visit> sortedVisits = new ArrayList<Visit>(getVisitsInternal());
		PropertyComparator.sort(sortedVisits, new MutableSortDefinition("date", false, false));
		return Collections.unmodifiableList(sortedVisits);
	}

	public void addVisit(Visit visit) {
		getVisitsInternal().add(visit);
		visit.setPet(this);
	}

    @SpaceRouting
    public Integer getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId)
    {
        this.ownerId = ownerId;
    }
}
