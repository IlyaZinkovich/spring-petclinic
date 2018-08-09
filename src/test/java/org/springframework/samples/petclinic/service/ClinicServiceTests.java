package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.owner.Address;
import org.springframework.samples.petclinic.owner.Name;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test of the Service and the Repository layer. <p> ClinicServiceSpringDataJpaTests
 * subclasses benefit from the following services provided by the Spring TestContext Framework: </p>
 * <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up time
 * between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances,
 * meaning that we don't need to perform application context lookups. See the use of {@link
 * Autowired @Autowired} on the <code>{@link ClinicServiceTests clinicService}</code> instance
 * variable, which uses autowiring <em>by type</em>. <li><strong>Transaction management</strong>,
 * meaning each test method is executed in its own transaction, which is automatically rolled back
 * by default. Thus, even if tests insert or otherwise change database state, there is no need for a
 * teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary.
 * </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class ClinicServiceTests {

  @Autowired
  protected OwnerRepository owners;

  @Autowired
  protected PetRepository pets;

  @Autowired
  protected VisitRepository visits;

  @Autowired
  protected VetRepository vets;

  @Test
  public void shouldFindOwnersByLastName() {
    Collection<Owner> owners = this.owners.findByLastName("Davis");
    assertThat(owners.size()).isEqualTo(2);

    owners = this.owners.findByLastName("Daviss");
    assertThat(owners.isEmpty()).isTrue();
  }

  @Test
  public void shouldFindSingleOwnerWithPet() {
    final int ownerId = 1;
    Owner owner = this.owners.findById(ownerId);
    assertThat(owner.name().lastName()).startsWith("Franklin");
    final List<Pet> petsOfOwner = this.pets.findByOwnerId(ownerId);
    assertThat(petsOfOwner.size()).isEqualTo(1);
    assertThat(petsOfOwner.get(0).getType()).isNotNull();
    assertThat(petsOfOwner.get(0).getType().getName()).isEqualTo("cat");
  }

  @Test
  @Transactional
  public void shouldInsertOwner() {
    Collection<Owner> owners = this.owners.findByLastName("Schultz");
    int found = owners.size();

    final Name name = new Name("Sam", "Schultz");
    final Address address = new Address("Wollongong", "4, Evans Street");
    final String telephone = "4444444444";
    final Owner owner = new Owner(name, address, telephone);
    this.owners.save(owner);
    assertThat(owner.id().longValue()).isNotEqualTo(0);

    owners = this.owners.findByLastName("Schultz");
    assertThat(owners.size()).isEqualTo(found + 1);
  }

  @Test
  @Transactional
  public void shouldUpdateOwner() {
    Owner owner = this.owners.findById(1);
    String oldLastName = owner.name().lastName();
    String newLastName = oldLastName + "X";

    owner.rename(new Name(owner.name().firstName(), newLastName));
    this.owners.save(owner);

    // retrieving new name from database
    owner = this.owners.findById(1);
    assertThat(owner.name()).isEqualTo(newLastName);
  }

  @Test
  public void shouldFindPetWithCorrectId() {
    final Pet pet = this.pets.findById(7);
    assertThat(pet.getName()).startsWith("Samantha");
  }

  @Test
  public void shouldFindAllPetTypes() {
    Collection<PetType> petTypes = this.pets.findPetTypes();

    PetType petType1 = getById(petTypes, 1, PetType::getId);
    assertThat(petType1.getName()).isEqualTo("cat");
    PetType petType4 = getById(petTypes, 4, PetType::getId);
    assertThat(petType4.getName()).isEqualTo("snake");
  }

  private <T> T getById(final Collection<T> collection, final int id,
      final Function<T, Integer> idExtractor) {
    return collection.stream()
        .filter(petType -> new Integer(id).equals(idExtractor.apply(petType)))
        .findAny()
        .orElseThrow(AssertionError::new);
  }

  @Test
  @Transactional
  public void shouldInsertPetIntoDatabaseAndGenerateId() {
    final Owner owner = this.owners.findById(6);
    final List<Pet> petsOfOwner = pets.findByOwnerId(6);
    final int found = petsOfOwner.size();

    final Pet pet = new Pet();
    pet.setName("bowser");
    final Collection<PetType> types = this.pets.findPetTypes();
    pet.setType(getById(types, 2, PetType::getId));
    pet.setBirthDate(LocalDate.now());
    pet.setOwnerId(6);

    this.pets.save(pet);

    assertThat(this.pets.findByOwnerId(6).size()).isEqualTo(found + 1);
    assertThat(pet.getId()).isNotNull();
  }

  @Test
  @Transactional
  public void shouldUpdatePetName() throws Exception {
    Pet pet7 = this.pets.findById(7);
    String oldName = pet7.getName();

    String newName = oldName + "X";
    pet7.setName(newName);
    this.pets.save(pet7);

    pet7 = this.pets.findById(7);
    assertThat(pet7.getName()).isEqualTo(newName);
  }

  @Test
  public void shouldFindVets() {
    Collection<Vet> vets = this.vets.findAll();

    Vet vet = getById(vets, 3, Vet::getId);
    assertThat(vet.getLastName()).isEqualTo("Douglas");
    assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
    assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
    assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
  }

  @Test
  @Transactional
  public void shouldAddNewVisitForPet() {
    Pet pet7 = this.pets.findById(7);
    int found = pet7.getVisits().size();
    Visit visit = new Visit();
    pet7.addVisit(visit);
    visit.setDescription("test");
    this.visits.save(visit);
    this.pets.save(pet7);

    pet7 = this.pets.findById(7);
    assertThat(pet7.getVisits().size()).isEqualTo(found + 1);
    assertThat(visit.getId()).isNotNull();
  }

  @Test
  public void shouldFindVisitsByPetId() throws Exception {
    Collection<Visit> visits = this.visits.findByPetId(7);
    assertThat(visits.size()).isEqualTo(2);
    Visit[] visitArr = visits.toArray(new Visit[visits.size()]);
    assertThat(visitArr[0].getDate()).isNotNull();
    assertThat(visitArr[0].getPetId()).isEqualTo(7);
  }

}
