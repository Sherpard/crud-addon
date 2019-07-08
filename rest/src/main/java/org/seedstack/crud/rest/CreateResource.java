/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.seed.transaction.Transactional;

/**
 * Specialization of {@link Resource} for creating aggregates (the C of CRUD).
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
public interface CreateResource<A extends AggregateRoot<I>, I, D> extends Resource<A, I, D> {
  /**
   * The method that implements REST aggregate creation.
   *
   * @param representation
   *          the representation of the aggregate as JSON request body.
   * @param uriInfo
   *          context injected by JAX-RS.
   * @return the JAX-RS {@link Response}.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  default Response create(D representation, @Context UriInfo uriInfo) {
    A aggregate = getFluentAssembler().merge(representation)
        .into(getAggregateRootClass())
        .fromFactory();

    try {
      getRepository().add(aggregate);
    } catch (AggregateExistsException e) {
      throw new ClientErrorException(buildAggregateName(aggregate.getId()) + " already exists",
          409);
    }

    return Response
        .created(uriInfo.getRequestUriBuilder().path(String.valueOf(aggregate.getId())).build())
        .entity(getFluentAssembler().assemble(aggregate).to(getRepresentationClass()))
        .build();
  }
}
