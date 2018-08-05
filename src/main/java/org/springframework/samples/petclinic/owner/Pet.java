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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.visit.Visit;

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
public class Pet implements Serializable {

  @Column(name = "birth_date")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private PetType type;

  @Column(name = "owner_id")
  private Integer ownerId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "petId", fetch = FetchType.EAGER)
  private Set<Visit> visits = new LinkedHashSet<>();
  @Column(name = "name")
  private String name;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  public LocalDate getBirthDate() {
    return this.birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public PetType getType() {
    return this.type;
  }

  public void setType(PetType type) {
    this.type = type;
  }

  protected Set<Visit> getVisitsInternal() {
    if (this.visits == null) {
      this.visits = new HashSet<>();
    }
    return this.visits;
  }

  protected void setVisitsInternal(Set<Visit> visits) {
    this.visits = visits;
  }

  public List<Visit> getVisits() {
    List<Visit> sortedVisits = new ArrayList<>(getVisitsInternal());
    PropertyComparator.sort(sortedVisits,
        new MutableSortDefinition("date", false, false));
    return Collections.unmodifiableList(sortedVisits);
  }

  public void addVisit(Visit visit) {
    getVisitsInternal().add(visit);
    visit.setPetId(this.getId());
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isNew() {
    return this.id == null;
  }

  public void setOwnerId(final int ownerId) {
    this.ownerId = ownerId;
  }
}
