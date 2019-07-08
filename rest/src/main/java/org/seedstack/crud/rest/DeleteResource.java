/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.seed.transaction.Transactional;

/**
 * Specialization of {@link Resource} for deleting aggregates (the D of CRUD).
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
 * @see Resource
 */
public interface DeleteResource<A extends AggregateRoot<I>, I, D> extends Resource<A, I, D> {

  /**
   * The method that implements REST aggregate deletion.
   *
   * @param id
   *          the identifier of the aggregate to delete, passed as {@code /{id}} path parameter. If
   *          the identifier type is a complex object, it must have a constructor taking a single
   *          {@link String} parameter.
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  default void delete(@PathParam("id") I id) {
    try {
      getRepository().remove(id);
    } catch (AggregateNotFoundException e) {
      throw new NotFoundException(buildAggregateName(id), e);
    }
  }
}
