package org.springframework.samples.petclinic.owner;

import static java.lang.String.format;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

  @Column(name = "first_name")
  private String firstName;
  @Column(name = "last_name")
  private String lastName;

  private Name() {
  }

  public Name(final String firstName, final String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String firstName() {
    return firstName;
  }

  public String lastName() {
    return lastName;
  }

  public String combined() {
    return format("%s %s", firstName, lastName);
  }
}
