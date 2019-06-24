/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.internal;

import com.google.inject.AbstractModule;

import java.util.Collection;


public class RestCrudModule extends AbstractModule {

  private final Collection<Class<?>> resources;

  RestCrudModule(Collection<Class<?>> resources) {
    this.resources = resources;
  }

  @Override
  protected void configure() {
    resources.stream().forEach(this::bind);
  }

}
