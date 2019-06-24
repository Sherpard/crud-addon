/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.model.animal;

import org.seedstack.business.domain.BaseValueObject;

public class AnimalId extends BaseValueObject {
  private static final long serialVersionUID = -5687053729759082639L;
  private String petName;

  public AnimalId(String petName) {
    this.petName = petName;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AnimalId other = (AnimalId) obj;
    if (petName == null) {
      if (other.petName != null) {
        return false;
      }
    } else if (!petName.equals(other.petName)) {
      return false;
    }
    return true;
  }

  public String getPetName() {
    return petName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((petName == null) ? 0 : petName.hashCode());
    return result;
  }

  public void setPetName(String petName) {
    this.petName = petName;
  }

  @Override
  public String toString() {
    return petName;
  }
}
