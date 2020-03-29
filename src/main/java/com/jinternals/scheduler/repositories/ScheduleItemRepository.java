package com.jinternals.scheduler.repositories;

import com.jinternals.scheduler.controllers.request.ScheduleItem;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleItemRepository extends CrudRepository<ScheduleItem, String> {

}
