/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.seedstack.business.domain.SortOption;

public class SortParams {
  @QueryParam("sort")
  private List<String> attributes;

  public SortOption buildSortOption() {
    SortOption sortOption = new SortOption();
    for (String attr : attributes) {
      if (attr.startsWith("-")) {
        sortOption.add(attr.substring(1), SortOption.Direction.DESCENDING);
      } else {
        sortOption.add(attr, SortOption.Direction.ASCENDING);
      }
    }
    return sortOption;
  }

  public boolean hasSort() {
    return !attributes.isEmpty();
  }
}
