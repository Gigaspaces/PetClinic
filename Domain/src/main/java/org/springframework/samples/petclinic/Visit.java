package org.springframework.samples.petclinic;

import com.gigaspaces.annotation.pojo.SpaceExclude;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity implements Serializable {

	/** Holds value of property date. */
    @Column(name = "visit_date")
    @Temporal(TemporalType.DATE)
    private Date date;

	/** Holds value of property description. */
	private String description;

	/** Holds value of property pet. */
    @Transient
    private Pet pet;

    @Column(name = "pet_id")
    private Integer petId;

    @Column(name = "pet_owner_id")
    private Integer petOwnerId;


    /** Creates a new instance of Visit for the current date */
	public Visit() {
		this.date = new Date();
	}

    public Visit(Integer id, Date date, String description) {
        super(id);
        this.date = date;
        this.description = description;
    }

    /** Getter for property date.
	 * @return Value of property date.
	 */
	public Date getDate() {
		return this.date;
	}

	/** Setter for property date.
	 * @param date New value of property date.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/** Getter for property description.
	 * @return Value of property description.
	 */
	public String getDescription() {
		return this.description;
	}

	/** Setter for property description.
	 * @param description New value of property description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/** Getter for property pet.
	 * @return Value of property pet.
	 */
    @SpaceExclude
    public Pet getPet() {
		return this.pet;
	}

	/** Setter for property pet.
	 * @param pet New value of property pet.
	 */
	protected void setPet(Pet pet) {
		this.pet = pet;
        if (pet != null) {
            this.petId = pet.getId();
            this.petOwnerId = pet.getOwnerId();
        }  else {
            this.petId = null;
            this.petOwnerId = null;
        }
    }

    protected Integer getPetId()
    {
        return petId;
    }

    protected void setPetId(Integer petId)
    {
        this.petId = petId;
    }

    @SpaceRouting
    public Integer getPetOwnerId()
    {
        return petOwnerId;
    }

    public void setPetOwnerId(Integer petOwnerId)
    {
        this.petOwnerId = petOwnerId;
    }
}
