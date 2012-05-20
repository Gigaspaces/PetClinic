package org.springframework.samples.petclinic;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Juergen Hoeller
 */
@Entity
@Table(name = "types")
public class PetType extends NamedEntity implements Serializable {
    public PetType() {}

    public PetType(Integer id, String name) {
        super(id, name);
    }
}
