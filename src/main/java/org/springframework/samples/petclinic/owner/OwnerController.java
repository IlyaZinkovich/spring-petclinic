/*
 * Copyright 2012-2018 the original author or authors.
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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
class OwnerController {

  private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
  private final OwnerRepository owners;

  public OwnerController(OwnerRepository clinicService) {
    this.owners = clinicService;
  }

  @InitBinder
  public void setAllowedFields(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  @GetMapping("/owners/new")
  public String initCreationForm(ModelAndView modelAndView) {
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }

  @PostMapping("/owners/new")
  public String processCreationForm(@Valid Owner owner, BindingResult result) {
    if (result.hasErrors()) {
      return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    } else {
      this.owners.save(owner);
      return "redirect:/owners/" + owner.id();
    }
  }

  @GetMapping("/owners/find")
  public String initFindForm(final Map<String, Object> model) {
    model.put("findOwnerQuery", new FindOwnerQuery());
    return "owners/findOwners";
  }

  @GetMapping("/owners")
  public String processFindForm(final FindOwnerQuery findOwnerQuery,
      final Map<String, Object> model) {
    final Collection<Owner> results =
        this.owners.findByLastName(Optional.ofNullable(findOwnerQuery.getLastName()).orElse(""));
    if (results.isEmpty()) {
      return "owners/findOwners";
    } else if (results.size() == 1) {
      final Owner owner = results.iterator().next();
      return format("redirect:/owners/%d", owner.id());
    } else {
      model.put("selections", results.stream().map(OwnerView::new).collect(toList()));
      return "owners/ownersList";
    }
  }

  @GetMapping("/owners/{ownerId}/edit")
  public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
    Optional.ofNullable(this.owners.findById(ownerId)).ifPresent(model::addAttribute);
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }

  @PostMapping("/owners/{ownerId}/edit")
  public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
      @PathVariable("ownerId") int ownerId) {
    if (result.hasErrors()) {
      return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    } else {
      this.owners.save(owner);
      return "redirect:/owners/{ownerId}";
    }
  }

  /**
   * Custom handler for displaying an owner.
   *
   * @param ownerId the ID of the owner to display
   * @return a ModelMap with the model attributes for the view
   */
  @GetMapping("/owners/{ownerId}")
  public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
    final ModelAndView mav = new ModelAndView("owners/ownerDetails");
    Optional.ofNullable(this.owners.findById(ownerId))
        .map(OwnerView::new)
        .ifPresent(ownerView -> mav.addObject("owner", ownerView));
    return mav;
  }

}
