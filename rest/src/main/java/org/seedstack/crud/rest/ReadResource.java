/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.pagination.Slice;
import org.seedstack.business.pagination.dsl.LimitPicker;
import org.seedstack.business.pagination.dsl.PaginationTypePicker;
import org.seedstack.business.pagination.dsl.SpecificationPicker;
import org.seedstack.business.specification.Specification;
import org.seedstack.seed.transaction.Transactional;

/**
 * Specialization of {@link Resource} for reading aggregates (the R of CRUD).
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
public interface ReadResource<A extends AggregateRoot<I>, I, D> extends Resource<A, I, D> {
  /**
   * Apply a pagination limit if present in the given pagination parameters.
   *
   * @param params
   *          the pagination parameters.
   * @param limitPicker
   *          the {@link org.seedstack.business.pagination.dsl.Paginator} DSL element to apply the
   *          limit to.
   * @param <S>
   *          the type of the paginated result.
   * @return the {@link org.seedstack.business.pagination.dsl.Paginator} DSL element to continue
   *         with.
   */
  default <S extends Slice<A>> SpecificationPicker<S, A> applyLimit(
      @BeanParam PaginationParams params,
      LimitPicker<S, A> limitPicker) {
    if (params.hasLimit()) {
      return limitPicker.limit(params.getLimit());
    } else {
      return limitPicker;
    }
  }

  /**
   * The method that implements REST aggregate retrieval.
   *
   * @param id
   *          the identifier of the aggregate to retrieve, passed as {@code /{id}} path parameter.
   *          If the identifier type is a complex object, it must have a constructor taking a single
   *          {@link String} parameter.
   * @return the representation of the retrieved aggregate.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  default D get(@PathParam("id") I id) {
    return getFluentAssembler().assemble(getRepository().get(id)
        .orElseThrow(() -> new NotFoundException(buildAggregateName(id) + " not found")))
        .to(getRepresentationClass());
  }

  /**
   * The method that implements REST aggregate listing. Supports pagination through
   * {@link PaginationParams} query parameters.
   *
   * @param paginationParams
   *          the optional pagination parameters.
   * @param sortParams
   *          the optional sorting parameters.
   * @return the serialized stream of aggregates, enveloped in a pagination wrapper if necessary.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  default Object list(@BeanParam PaginationParams paginationParams,
      @BeanParam SortParams sortParams) {
    final Specification<A> filterSpec = Specification.any();

    Repository.Option[] options;
    if (sortParams.hasSort()) {
      options = new Repository.Option[] { sortParams.buildSortOption() };
    } else {
      options = new Repository.Option[0];
    }

    if (paginationParams.hasPagination()) {
      PaginationTypePicker<A> paginationTypePicker = getPaginator().paginate(getRepository())
          .withOptions(options);
      if (paginationParams.isAttributeBased()) {
        return getFluentAssembler().assemble(applyLimit(paginationParams, paginationTypePicker
            .byAttribute(paginationParams.getAttribute())
            .after(paginationParams.getValue()))
                .matching(filterSpec))
            .toSliceOf(getRepresentationClass());
      } else if (paginationParams.isOffsetBased()) {
        return getFluentAssembler().assemble(applyLimit(paginationParams, paginationTypePicker
            .byOffset(paginationParams.getOffset()))
                .matching(filterSpec))
            .toSliceOf(getRepresentationClass());
      } else if (paginationParams.isPageBased()) {
        return getFluentAssembler().assemble(applyLimit(paginationParams, paginationTypePicker
            .byPage(paginationParams.getPage()))
                .matching(filterSpec))
            .toPageOf(getRepresentationClass());
      } else {
        throw new IllegalArgumentException("Missing pagination parameters");
      }
    } else {
      return getFluentAssembler().assemble(getRepository().get(filterSpec, options))
          .toStreamOf(getRepresentationClass());
    }
  }
}
