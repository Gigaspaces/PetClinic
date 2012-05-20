package com.gigaspaces.dae.petclinic.processor.init;

import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.*;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DataInitializer {
    Logger log = LoggerFactory.getLogger(this.getClass());

    GigaSpace space;

    @Autowired
    public void setGigaSpace(GigaSpace space) {
        this.space = space;
    }

    public GigaSpace getGigaSpace() {
        return this.space;
    }

    @PostConstruct
    public void populateDataModel() {
        checkVets();
        checkPetTypes();
        checkOwners();
        checkPets();
    }

    private void checkPets() {
        if(getGigaSpace().read(new Pet())==null) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            try {
                getGigaSpace().write(new Pet(1, "Leo",      sdf.parse("2000-09-07"), getPetType(1), getOwner(1)));
                getGigaSpace().write(new Pet(2, "Basil",    sdf.parse("2002-08-06"), getPetType(6), getOwner(2)));
                getGigaSpace().write(new Pet(3, "Rosy",     sdf.parse("2001-04-17"), getPetType(2), getOwner(3)));
                getGigaSpace().write(new Pet(4, "Jewel",    sdf.parse("2000-03-07"), getPetType(2), getOwner(3)));
                getGigaSpace().write(new Pet(5, "Iggy",     sdf.parse("2000-11-30"), getPetType(3), getOwner(4)));
                getGigaSpace().write(new Pet(6, "George",   sdf.parse("2000-01-20"), getPetType(4), getOwner(5)));
                getGigaSpace().write(new Pet(7, "Samantha", sdf.parse("1995-09-04"), getPetType(1), getOwner(6),
                        new Visit(1, sdf.parse("1996-03-04"), "rabies shot"),
                        new Visit(4, sdf.parse("1996-09-04"), "spayed")));
                getGigaSpace().write(new Pet(8, "Max",      sdf.parse("1995-09-04"), getPetType(1), getOwner(6),
                        new Visit(2, sdf.parse("1996-03-04"), "rabies shot"),
                        new Visit(3, sdf.parse("1996-06-04"), "neutered")));
                getGigaSpace().write(new Pet(9, "Lucky",    sdf.parse("1999-08-06"), getPetType(5), getOwner(7)));
                getGigaSpace().write(new Pet(10, "Mulligan", sdf.parse("1997-02-24"), getPetType(2), getOwner(8)));
                getGigaSpace().write(new Pet(11, "Freddy",   sdf.parse("2000-03-09"), getPetType(5), getOwner(9)));
                getGigaSpace().write(new Pet(12, "Lucky",    sdf.parse("2000-06-24"), getPetType(2), getOwner(10)));
                getGigaSpace().write(new Pet(13, "Sly",      sdf.parse("2002-06-08"), getPetType(1), getOwner(10)));
            } catch (ParseException e) {
                e.printStackTrace();  
            }
        }
    }

    private Owner getOwner(int i) {
        Owner template=new Owner();
        template.setId(i);
        return getGigaSpace().read(template);
    }

    private PetType getPetType(int i) {
        PetType template=new PetType();
        template.setId(i);
        return getGigaSpace().read(template);
    }

    private void checkOwners() {
        Owner template = new Owner();
        if (getGigaSpace().read(template) == null) {
            String[][] ownerData = {
                    {"George", "Franklin",    "110 W. Liberty St.",   "Madison",     "6085551023"},
                    {"Betty", "Davis",       "638 Cardinal Ave.",     "Sun Prairie", "6085551749"},
                    {"Eduardo", "Rodriguez", "2693 Commerce St..",    "McFarland",   "6085558763"},
                    {"Harold", "Davis",      "563 Friendly St.",      "Windsor",     "6085553198"},
                    {"Peter", "McTavish",    "2387 Fair Way",         "Madison",     "6085552765"},
                    {"Jean", "Coleman",      "105 N. Lake St.",       "Monona",      "6085552654"},
                    {"Jeff", "Black",        "1450 Oak Blvd.",        "Monona",      "6085555387"},
                    {"Maria", "Escobito",    "345 Maple St.",         "Madison",     "6085557683"},
                    {"David", "Schroeder",   "2749 Blackhawk Trail",  "Madison",     "6085559435"},
                    {"Carlos", "Estaban",    "2335 Independence La.", "Waunakee",    "6085555487"},
            };
            int index=0;
            for(String[] data:ownerData) {
                Owner owner=new Owner(++index, data[0], data[1], data[2], data[3], data[4]);
                getGigaSpace().write(owner);
            }
        }
    }

    private void checkPetTypes() {
        PetType template = new PetType();
        if (getGigaSpace().read(template) == null) {
            String[] types = {"cat", "dog", "lizard", "snake", "bird", "hamster"};
            int index = 0;
            for (String type : types) {
                getGigaSpace().write(new PetType(++index, type));
            }
        }
    }

    private void checkVets() {
        Vet template = new Vet();
        Specialty specialties[] = {
                new Specialty(1, "radiology"),
                new Specialty(2, "surgery"),
                new Specialty(3, "dentistry"),
        };
        if (getGigaSpace().read(template) == null) {
            log.info("Populating veterinarians.");
            getGigaSpace().write(new Vet(1, "James", "Carter"));
            getGigaSpace().write(new Vet(2, "Helen", "Leary", specialties[0]));
            getGigaSpace().write(new Vet(3, "Linda", "Douglas", specialties[1], specialties[2]));
            getGigaSpace().write(new Vet(4, "Rafael", "Ortega", specialties[1]));
            getGigaSpace().write(new Vet(5, "Henry", "Stevens", specialties[0]));
            getGigaSpace().write(new Vet(6, "Sharon", "Jenkins"));
        }
    }

}
