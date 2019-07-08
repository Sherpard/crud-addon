/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.seed.transaction.Transactional;

/**
 * Specialization of {@link Resource} for updating aggregates (the U of CRUD).
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
public interface UpdateResource<A extends AggregateRoot<I>, I, D> extends Resource<A, I, D> {
  /**
   * The method that implements REST aggregate updating.
   *
   * @param id
   *          the identifier of the aggregate to update, passed as {@code /{id}} path parameter. If
   *          the identifier type is a complex object, it must have a constructor taking a single
   *          {@link String} parameter.
   * @param representation
   *          the updated representation of the aggregate as JSON request body.
   * @return the updated representation.
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  @Transactional
  default D update(@PathParam("id") I id, D representation) {
    A aggregate;
    try {
      aggregate = getFluentAssembler().merge(representation)
          .into(getAggregateRootClass())
          .fromRepository()
          .orFail();
    } catch (AggregateNotFoundException e) {
      throw new NotFoundException(buildAggregateName(id) + " not found");
    }
    return getFluentAssembler().assemble(getRepository().update(aggregate))
        .to(getRepresentationClass());
  }
}
