/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.seedstack.crud;

import javax.ws.rs.Path;

import org.seedstack.crud.fixtures.Item1;
import org.seedstack.crud.fixtures.Item1Representation;
import org.seedstack.crud.rest.BaseCrudResource;

import io.swagger.annotations.Api;

@Api
@Path("/item-explicit")
public class ExplicitCrud extends BaseCrudResource<Item1, Long, Item1Representation> {

}
