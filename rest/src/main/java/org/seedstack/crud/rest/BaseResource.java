/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import java.lang.reflect.Type;

import javax.inject.Inject;

import org.seedstack.business.assembler.dsl.FluentAssembler;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.internal.utils.BusinessUtils;
import org.seedstack.business.pagination.dsl.Paginator;

//TODO:Test BaseResource Alone

/**
 * This base class for CRUD resources provides the necessary infrastructure without defining any
 * JAX-RS mapping. It can be used as a base to selectively combine CRUD operations in a class which
 * would implement one or more of the {@link CreateResource}, {@link ReadResource},
 * {@link UpdateResource} and {@link DeleteResource} interfaces.
 *
 * @param <A>
 *          the aggregate root type.
 * @param <I>
 *          the aggregate root identifier type.
 * @param <D>
 *          the representation type.
 */
public abstract class BaseResource<A extends AggregateRoot<I>, I, D> implements Resource<A, I, D> {
  private final Class<A> aggregateRootClass;
  private final Class<I> identifierClass;
  private final Class<D> representationClass;
  @Inject
  private DomainRegistry domainRegistry;
  @Inject
  private FluentAssembler fluentAssembler;
  @Inject
  private Paginator paginator;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected BaseResource() {
    Type[] generics = BusinessUtils.resolveGenerics(BaseResource.class, this.getClass());
    this.aggregateRootClass = (Class) generics[0];
    this.identifierClass = (Class) generics[1];
    this.representationClass = (Class) generics[2];
  }

  protected BaseResource(Class<A> aggregateRootClass, Class<I> identifierClass,
      Class<D> representationClass) {
    this.aggregateRootClass = aggregateRootClass;
    this.identifierClass = identifierClass;
    this.representationClass = representationClass;
  }

  @Override
  public Class<A> getAggregateRootClass() {
    return aggregateRootClass;
  }

  @Override
  public FluentAssembler getFluentAssembler() {
    return fluentAssembler;
  }

  @Override
  public Class<I> getIdentifierClass() {
    return identifierClass;
  }

  @Override
  public Paginator getPaginator() {
    return paginator;
  }

  @Override
  public Repository<A, I> getRepository() {
    return domainRegistry.getRepository(aggregateRootClass, identifierClass);
  }

  @Override
  public Class<D> getRepresentationClass() {
    return representationClass;
  }
}
