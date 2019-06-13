/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.crud.rest;

import static io.restassured.RestAssured.given;
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
import org.seedstack.crud.rest.fixtures.model.customer.Customer;
import org.seedstack.crud.rest.fixtures.model.customer.CustomerId;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.undertow.LaunchWithUndertow;

@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public class ExplicitResourceIT {
  @Inject
  private Repository<Customer, CustomerId> customerRepository;

  @Configuration("runtime.web.baseUrlSlash")
  private String url;

  @Test
  public void attributePaginatedList() {
    when().get(url + "customers?sort=id.firstName&attribute=id.firstName&value=Jeanne&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("items", hasSize(2))
        .body("items.firstName", contains("Michael", "Robert"));
  }

  @Test
  public void create() {
    given().body("{\"firstName\":\"Tara\", \"lastName\":\"JOHNSON\"}")
        .contentType("application/json")
        .when()
        .post(url + "customers")
        .then()
        .statusCode(201)
        .header("Location", url + "customers/Tara%20JOHNSON");
  }

  @Test
  public void delete() {
    when()
        .delete(url + "customers/Robert DENIRO")
        .then()
        .statusCode(404);
    when()
        .get(url + "customers/Robert SMITH")
        .then()
        .statusCode(200);
    when()
        .delete(url + "customers/Robert SMITH")
        .then()
        .statusCode(204);
    when()
        .get(url + "customers/Robert SMITH")
        .then()
        .statusCode(404);
  }

  @Test
  public void get() {
    when().get(url + "customers/Robert SMITH")
        .then()
        .statusCode(200)
        .body("firstName", equalTo("Robert"))
        .body("lastName", equalTo("SMITH"));
  }

  @Test
  public void list() {
    when().get(url + "customers?sort=id.firstName")
        .then()
        .statusCode(200)
        .body("$", hasSize(3))
        .body("firstName", contains("Jeanne", "Michael", "Robert"));
  }

  @Test
  public void offsetPaginatedList() {
    when().get(url + "customers?sort=id.firstName&offset=1&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("items", hasSize(2))
        .body("items.firstName", contains("Michael", "Robert"));
  }

  @Test
  public void pagePaginatedList() {
    when().get(url + "customers?sort=id.firstName&page=1&limit=2")
        .then()
        .statusCode(200)
        .body("size", equalTo(2))
        .body("index", equalTo(1))
        .body("maxSize", equalTo(2))
        .body("totalSize", equalTo(3))
        .body("items", hasSize(2))
        .body("items.firstName", contains("Jeanne", "Michael"));
  }

  /***
   * <p> Setup customer repository for integration tests. </p>
   */
  @Before
  public void setUp() {
    customerRepository.add(new Customer(new CustomerId("Robert", "SMITH")));
    customerRepository.add(new Customer(new CustomerId("Jeanne", "O'GRADY")));
    customerRepository.add(new Customer(new CustomerId("Michael", "JONES")));
  }

  @After
  public void tearDown() {
    customerRepository.clear();
  }

  @Test
  public void update() {
    given().body("{\"firstName\":\"Robert\", \"lastName\":\"SMITH\", \"age\": 35}")
        .contentType("application/json")
        .when()
        .put(url + "customers/Robert SMITH")
        .then()
        .statusCode(200)
        .body("firstName", equalTo("Robert"))
        .body("lastName", equalTo("SMITH"))
        .body("age", equalTo(35));
  }
}
