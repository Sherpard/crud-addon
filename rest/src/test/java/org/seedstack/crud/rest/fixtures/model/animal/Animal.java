/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.crud.rest.fixtures.model.animal;

import org.seedstack.business.domain.BaseAggregateRoot;

public class Animal extends BaseAggregateRoot<AnimalId> {
  private AnimalId id;
  private Long daysInHome;

  public Animal(AnimalId id) {
    this.id = id;
  }

  public Long getDaysInHome() {
    return daysInHome;
  }

  @Override
  public AnimalId getId() {
    return id;
  }

  public void setDaysInHome(Long daysInHome) {
    this.daysInHome = daysInHome;
  }
}
