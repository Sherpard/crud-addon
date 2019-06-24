/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.rest.animal;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.crud.rest.fixtures.model.animal.Animal;

public abstract class AnimalRepresentationAssembler<A extends Animal, R extends AnimalRepresentation>
    extends BaseAssembler<A, R> {

  @Override
  public void mergeAggregateIntoDto(A a, R r) {
    r.setName(a.getId().getPetName());
    r.setDaysInHome(a.getDaysInHome());
  }

  @Override
  public void mergeDtoIntoAggregate(R r, A a) {
    a.setDaysInHome(r.getDaysInHome());
  }
}
