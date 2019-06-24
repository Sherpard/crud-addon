/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import org.seedstack.business.domain.AggregateRoot;

/**
 * Base class implementing all the CRUD operations. Extend this class when all CRUD operations
 * should be exposed for a particular aggregate/representation couple.
 *
 * @param <A>
 *          the aggregate root type.
 * @param <I>
 *          the aggregate root identifier type.
 * @param <D>
 *          the representation type.
 */
public abstract class BaseCrudResource<A extends AggregateRoot<I>, I, D>
    extends BaseResource<A, I, D> implements
    CreateResource<A, I, D>,
    ReadResource<A, I, D>,
    UpdateResource<A, I, D>,
    DeleteResource<A, I, D> {

  protected BaseCrudResource() {
    super();
  }

  protected BaseCrudResource(Class<A> aggregateRootClass, Class<I> identifierClass,
      Class<D> representationClass) {
    super(aggregateRootClass, identifierClass, representationClass);
  }
}
