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
package org.seedstack.crud.fixtures;

import java.io.Serializable;

import org.seedstack.business.assembler.AggregateId;
import org.seedstack.business.assembler.DtoOf;
import org.seedstack.crud.rest.RestCrud;

@DtoOf(Item1.class)
@RestCrud(value = "item", create = true, delete = true, update = true, read = true)
public class Item1Representation implements Serializable {

    private static final long serialVersionUID = -4322043372003873013L;
    private Long id;
    private String name;

    @AggregateId
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
