package org.seedstack.crud.fixtures;

import org.seedstack.business.assembler.BaseAssembler;

public class Item1Assembler extends BaseAssembler<Item1, Item1Representation> {

  @Override
  public void mergeAggregateIntoDto(Item1 sourceAggregate, Item1Representation targetDto) {

    targetDto.setId(sourceAggregate.getId());

    targetDto.setName(sourceAggregate.getName());
  }

  @Override
  public void mergeDtoIntoAggregate(Item1Representation sourceDto, Item1 targetAggregate) {

    targetAggregate.setId(sourceDto.getId());
    targetAggregate.setName(sourceDto.getName());
  }
}
