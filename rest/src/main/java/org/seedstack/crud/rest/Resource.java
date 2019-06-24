/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import org.seedstack.business.assembler.dsl.FluentAssembler;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.pagination.dsl.Paginator;

/**
 * Common interface shared by all CRUD-specialized interfaces.
 *
 * @param <A>
 *          the aggregate root type.
 * @param <I>
 *          the aggregate root identifier type.
 * @param <D>
 *          the representation type.
 * @see CreateResource
 * @see ReadResource
 * @see UpdateResource
 * @see DeleteResource
 */
public interface Resource<A extends AggregateRoot<I>, I, D> {
  /**
   * Builds the standardized name of the aggregate for a particular id.
   *
   * @param id
   *          the aggregate identifier.
   * @return the aggregate name.
   */
  default String buildAggregateName(I id) {
    return getAggregateRootClass().getSimpleName() + "[" + id + "]";
  }

  /**
   * Returns the aggregate root class managed by the resource.
   *
   * @return the aggregate root class.
   */
  Class<A> getAggregateRootClass();

  /**
   * Returns the {@link FluentAssembler} implementation used by the resource.
   *
   * @return the {@link FluentAssembler} implementation.
   */
  FluentAssembler getFluentAssembler();

  /**
   * Returns the aggregate root identifier class managed by the resource.
   *
   * @return the aggregate root identifier class.
   */
  Class<I> getIdentifierClass();

  /**
   * Returns the {@link Paginator} implementation used by the resource.
   *
   * @return the {@link Paginator} implementation.
   */
  Paginator getPaginator();

  /**
   * Returns the repository where to find aggregates managed by the resource.
   *
   * @return the repository of managed aggregates.
   */
  Repository<A, I> getRepository();

  /**
   * Returns the representation class managed by the resource.
   *
   * @return the representation class.
   */
  Class<D> getRepresentationClass();
}
