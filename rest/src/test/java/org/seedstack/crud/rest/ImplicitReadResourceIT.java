/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.crud.rest.fixtures.model.animal.AnimalId;
import org.seedstack.crud.rest.fixtures.model.animal.Bug;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.undertow.LaunchWithUndertow;

@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public class ImplicitReadResourceIT {
  @Configuration("runtime.web.baseUrlSlash")
  private String url;

  @Inject
  private Repository<Bug, AnimalId> bugRepository;

  @Before
  public void setUp() {
    bugRepository.add(new Bug(new AnimalId("dee-dee")));
    bugRepository.add(new Bug(new AnimalId("joey")));
    bugRepository.add(new Bug(new AnimalId("marky")));
  }

  @After
  public void tearDown() {
    bugRepository.clear();
  }

  @Test
  public void attributePaginatedList() {
    when().get(url + "bugs?sort=id.petName&attribute=id.petName&value=dee-dee&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("items", hasSize(2))
        .body("items.name", contains("joey", "marky"));
  }

  @Test
  public void get() {
    when().get(url + "bugs/joey")
        .then()
        .statusCode(200)
        .body("name", equalTo("joey"));
  }

  @Test
  public void list() {
    when().get(url + "bugs?sort=id.petName")
        .then()
        .statusCode(200)
        .body("name", contains("dee-dee", "joey", "marky"));
  }

  @Test
  public void offsetPaginatedList() {
    when().get(url + "bugs?sort=id.petName&offset=1&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("items", hasSize(2))
        .body("items.name", contains("joey", "marky"));
  }

  @Test
  public void pagePaginatedList() {
    when().get(url + "bugs?sort=id.petName&page=1&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("index", equalTo(1))
        .body("maxSize", equalTo(2))
        .body("totalSize", equalTo(3))
        .body("items", hasSize(2))
        .body("items.name", contains("dee-dee", "joey"));
  }
}
