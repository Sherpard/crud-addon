/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.crud.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.crud.rest.fixtures.model.animal.AnimalId;
import org.seedstack.crud.rest.fixtures.model.animal.Cat;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.undertow.LaunchWithUndertow;

@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public class ImplicitUpdateResourceIT {
  @Configuration("runtime.web.baseUrlSlash")
  private String url;

  @Inject
  private Repository<Cat, AnimalId> catRepository;

  @Before
  public void setUp() {
    catRepository.add(new Cat(new AnimalId("felix")));
  }

  @After
  public void tearDown() {
    catRepository.clear();
  }

  @Test
  public void testUpdate() {
    given().body("{\"name\":\"felix\", \"daysInHome\": 12}")
        .contentType("application/json")
        .when()
        .put(url + "cats/felix")
        .then()
        .statusCode(200)
        .body("name", equalTo("felix"))
        .body("daysInHome", equalTo(12));
  }
  
  @Test
  public void testUpdateUnexistent() {
    given().body("{\"name\":\"pluto\", \"daysInHome\": 5}")
        .contentType("application/json")
        .when()
        .put(url + "cats/pluto")
        .then()
        .statusCode(404);
  }
  
}
