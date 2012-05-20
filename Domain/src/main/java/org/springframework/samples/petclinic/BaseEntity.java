package org.springframework.samples.petclinic;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Simple JavaBean domain object with an id property.
 * Used as a base class for objects needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass 
public class BaseEntity implements Serializable {

    @Id
    private Integer id;

    public BaseEntity() {}

    public BaseEntity(Integer id) {
        this.id = id;
    }

    public void setId(Integer id) {
		this.id = id;
	}

    @SpaceId(autoGenerate = false)
    @SpaceRouting
    public Integer getId() {
		return id;
	}

	public boolean isNew() {
		return (this.id == null);
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o.getClass().equals(this.getClass()))) return false;
        BaseEntity that = (BaseEntity) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
