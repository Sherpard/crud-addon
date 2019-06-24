/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import javax.ws.rs.QueryParam;

/**
 * Groups all JAX-RS query parameters supporting the various pagination modes (attribute based,
 * offset-based and page-based).
 */
public class PaginationParams {
  @QueryParam("attribute")
  private String attribute;
  @QueryParam("limit")
  private Long limit;
  @QueryParam("offset")
  private Long offset;
  @QueryParam("page")
  private Long page;
  @QueryParam("value")
  private String value;

  /**
   * If pagination is attribute-based, returns the attribute that should be used.
   *
   * @return the path to the attribute used for attribute-based pagination.
   */
  public String getAttribute() {
    return attribute;
  }

  /**
   * Returns the limit if it has been specified.
   *
   * @return the limit if specified, null otherwise.
   */
  public Long getLimit() {
    return limit;
  }

  /**
   * If pagination is offset-based, returns the number of items to skip.
   *
   * @return the number of items to skip.
   */
  public Long getOffset() {
    return offset;
  }

  /**
   * If pagination is page-based, returns the index of the page. Whether this index is 0-based or
   * 1-based, depends on the business framework configuration.
   *
   * @return the index of the page.
   */
  public Long getPage() {
    return page;
  }

  /**
   * If pagination is attribute-based, returns the value that should act as the upper value.
   *
   * @return the value used as the upper value for attribute-based pagination.
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns true if a limit has been specified.
   *
   * @return true if a limit has been specified, false otherwise.
   */
  public boolean hasLimit() {
    return limit != null;
  }

  /**
   * Returns if pagination should be enabled.
   *
   * @return true if pagination should be enabled, false otherwise.
   */
  public boolean hasPagination() {
    return isAttributeBased() || isPageBased() || isOffsetBased();
  }

  /**
   * Returns if pagination is attribute based.
   *
   * @return true if pagination is attribute based, false otherwise.
   */
  public boolean isAttributeBased() {
    return attribute != null;
  }

  /**
   * Returns if pagination is offset based.
   *
   * @return true if pagination is offset based, false otherwise.
   */
  public boolean isOffsetBased() {
    return offset != null;
  }

  /**
   * Returns if pagination is page based.
   *
   * @return true if pagination is page based, false otherwise.
   */
  public boolean isPageBased() {
    return page != null;
  }
}
