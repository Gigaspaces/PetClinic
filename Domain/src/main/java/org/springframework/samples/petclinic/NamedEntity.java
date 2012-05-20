package org.springframework.samples.petclinic;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>.
 * Used as a base class for objects needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity implements Serializable{

    @Column(name = "NAME")
    private String name;

    public NamedEntity() {}

    public NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.getName();
	}

}
