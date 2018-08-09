package org.springframework.samples.petclinic.owner;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

  @Column(name = "address_city")
  private String city;
  @Column(name = "address_first_line")
  private String firstLine;

  private Address() {
  }

  public Address(final String city, final String firstLine) {
    this.city = city;
    this.firstLine = firstLine;
  }

  public String city() {
    return city;
  }

  public String firstLine() {
    return firstLine;
  }
}
