/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.rest.customer;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.crud.rest.fixtures.model.customer.Customer;

public class CustomRepresentationAssembler extends BaseAssembler<Customer, CustomerRepresentation> {
  @Override
  public void mergeAggregateIntoDto(Customer customer,
      CustomerRepresentation customerRepresentation) {
    customerRepresentation.setFirstName(customer.getId().getFirstName());
    customerRepresentation.setLastName(customer.getId().getLastName());
    customerRepresentation.setAge(customer.getAge());
  }

  @Override
  public void mergeDtoIntoAggregate(CustomerRepresentation customerRepresentation,
      Customer customer) {
    customer.setAge(customerRepresentation.getAge());
  }
}
