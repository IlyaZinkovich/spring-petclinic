package org.springframework.samples.petclinic.owner;

public class OwnerView {

  private final Integer id;
  private final String name;
  private final String addressCity;
  private final String addressFirstLine;
  private final String telephone;

  public OwnerView(final Owner owner) {
    this.id = owner.id();
    this.name = owner.name().combined();
    this.addressCity = owner.address().city();
    this.addressFirstLine = owner.address().firstLine();
    this.telephone = owner.telephone();
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddressCity() {
    return addressCity;
  }

  public String getAddressFirstLine() {
    return addressFirstLine;
  }

  public String getTelephone() {
    return telephone;
  }
}
