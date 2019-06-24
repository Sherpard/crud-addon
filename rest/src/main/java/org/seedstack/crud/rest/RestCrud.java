/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be applied on REST representations (DTO) also annotated with
 * {@link org.seedstack.business.assembler.DtoOf} to generate a CRUD resource on the specified path.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface RestCrud {
  /**
   * Defines if the generated resource should allow creation.
   *
   * @return true if creation is allowed, false otherwise.
   */
  boolean create() default true;

  /**
   * Defines if the generated resource should allow deletion.
   *
   * @return true if deletion is allowed, false otherwise.
   */
  boolean delete() default true;

  /**
   * Defines if the generated resource should allow reading.
   *
   * @return true if reading is allowed, false otherwise.
   */
  boolean read() default true;

  /**
   * Defines if the generated resource should allow updating.
   *
   * @return true if updating is allowed, false otherwise.
   */
  boolean update() default true;

  /**
   * The path the CRUD resource should be exposed on.
   *
   * @return the resource path.
   */
  String value() default "";
}
