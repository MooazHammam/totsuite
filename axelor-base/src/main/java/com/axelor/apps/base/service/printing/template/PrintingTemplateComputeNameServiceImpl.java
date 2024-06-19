/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.base.service.printing.template;

import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.printing.template.model.PrintingGenFactoryContext;
import com.axelor.db.EntityHelper;
import com.axelor.db.Model;
import com.axelor.db.mapper.Mapper;
import com.axelor.text.GroovyTemplates;
import com.axelor.text.Templates;
import com.axelor.utils.service.TranslationBaseService;
import com.google.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PrintingTemplateComputeNameServiceImpl implements PrintingTemplateComputeNameService {

  protected GroovyTemplates groovyTemplates;
  protected TranslationBaseService translationBaseService;
  protected AppBaseService appBaseService;

  @Inject
  public PrintingTemplateComputeNameServiceImpl(
      GroovyTemplates groovyTemplates,
      TranslationBaseService translationBaseService,
      AppBaseService appBaseService) {
    this.groovyTemplates = groovyTemplates;
    this.translationBaseService = translationBaseService;
    this.appBaseService = appBaseService;
  }

  @Override
  public String computeFileName(String name, PrintingGenFactoryContext factoryContext) {
    name = translationBaseService.getValueTranslation(name);

    if (factoryContext == null) {
      return name;
    }

    Map<String, Object> templatesContext = new HashMap<>(factoryContext.getContext());
    Model model = factoryContext.getModel();

    if (model != null) {
      Class<?> klass = EntityHelper.getEntityClass(model);
      templatesContext.put(klass.getSimpleName(), Mapper.toMap(model));
    }
    ZonedDateTime todayDateTime = appBaseService.getTodayDateTime();
    templatesContext.put("date", todayDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    templatesContext.put("time", todayDateTime.format(DateTimeFormatter.ofPattern("HHmmss")));
    templatesContext.put(
        "datetime", todayDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    return getTemplateEngine().fromText(name).make(templatesContext).render();
  }

  protected Templates getTemplateEngine() {
    return groovyTemplates;
  }
}
