/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "owners")
public class Owner implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Embedded
  private Name name;
  @Embedded
  private Address address;
  private String phoneNumber;

  private Owner() {

  }

  public Owner(final Integer id, final Name name, final Address address, final String phoneNumber) {
    this(name, address, phoneNumber);
    this.id = id;
  }

  public Owner(final Name name, final Address address, final String phoneNumber) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  public Integer id() {
    return id;
  }

  public Name name() {
    return this.name;
  }

  public Address address() {
    return this.address;
  }

  public String phoneNumber() {
    return this.phoneNumber;
  }

  public void rename(final Name name) {
    this.name = name;
  }
}
