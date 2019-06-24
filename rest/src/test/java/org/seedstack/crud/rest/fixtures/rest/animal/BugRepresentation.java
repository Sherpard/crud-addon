/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.rest.animal;

import org.seedstack.business.assembler.DtoOf;
import org.seedstack.crud.rest.RestCrud;
import org.seedstack.crud.rest.fixtures.model.animal.Bug;

@DtoOf(Bug.class)
@RestCrud(value = "bugs", create = false, read = true, update = false, delete = false)
public class BugRepresentation extends AnimalRepresentation {
}
