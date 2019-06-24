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
import org.seedstack.crud.rest.fixtures.model.animal.Dog;

//Value left uncovered on purpose to test resource name generation logic
@DtoOf(Dog.class)
@RestCrud(create = false, read = true, update = false, delete = true)
public class DogRepresentation extends AnimalRepresentation {
}
