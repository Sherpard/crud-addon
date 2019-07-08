/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.crud.fixtures.Item1;
import org.seedstack.jpa.Jpa;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.undertow.LaunchWithUndertow;

import io.restassured.RestAssured;

@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public class JpaIntegrationTest {

    @Inject
    @Jpa
    private Repository<Item1, Long> repository;

    @Configuration("runtime.web.baseUrlSlash")
    private String url;

    @Before
    @Transactional
    @JpaUnit
    public void setUp() throws Exception {

        Item1 item = new Item1();
        item.setId(1L);

        repository.add(item);
    }

    @After
    @Transactional
    @JpaUnit
    public void tearDown() throws Exception {
        repository.remove(1L);
    }

    @Test
    public void testGet() throws Exception {
        RestAssured.given().get(url + "item-explicit/1").then().statusCode(200);
    }
}
