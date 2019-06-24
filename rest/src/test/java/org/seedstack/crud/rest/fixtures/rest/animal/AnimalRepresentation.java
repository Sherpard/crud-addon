/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.rest.animal;

import org.seedstack.business.assembler.AggregateId;
import org.seedstack.business.assembler.FactoryArgument;
import org.seedstack.crud.rest.fixtures.model.animal.AnimalId;

public abstract class AnimalRepresentation {
  private String name;
  private Long daysInHome;

  public Long getDaysInHome() {
    return daysInHome;
  }

  @AggregateId
  @FactoryArgument
  public AnimalId getId() {
    return new AnimalId(name);
  }

  public String getName() {
    return name;
  }

  public void setDaysInHome(Long daysInHome) {
    this.daysInHome = daysInHome;
  }

  public void setName(String birdId) {
    name = birdId;
  }
}
