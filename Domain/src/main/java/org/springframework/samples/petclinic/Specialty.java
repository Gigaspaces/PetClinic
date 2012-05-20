package org.springframework.samples.petclinic;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Models a {@link Vet Vet's} specialty (for example, dentistry).
 * 
 * @author Juergen Hoeller
 */
@Entity
@Table(name = "specialties")
public class Specialty extends NamedEntity implements Serializable {
    public Specialty() {}

    public Specialty(Integer id, String name) {
        super(id, name);
    }
}
