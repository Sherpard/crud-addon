/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.fixtures.rest.customer;

import org.seedstack.business.assembler.AggregateId;
import org.seedstack.business.assembler.DtoOf;
import org.seedstack.business.assembler.FactoryArgument;
import org.seedstack.crud.rest.fixtures.model.customer.Customer;
import org.seedstack.crud.rest.fixtures.model.customer.CustomerId;

@DtoOf(Customer.class)
public class CustomerRepresentation {
  private int age;
  private String firstName;
  private String lastName;

  public int getAge() {
    return age;
  }

  @AggregateId
  @FactoryArgument
  public CustomerId getCustomerId() {
    return new CustomerId(firstName, lastName);
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
