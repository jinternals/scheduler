package com.jinternals.scheduler.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinternals.scheduler.controllers.request.ScheduleItem;
import com.jinternals.scheduler.services.SchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SchedulerController.class)
public class SchedulerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SchedulerService schedulerService;

    @Test
    public void shouldScheduleItem() throws Exception {

        ScheduleItem scheduleItem = new ScheduleItem("some-id", Instant.now());

        given(this.schedulerService.scheduleItem(scheduleItem))
                .willReturn(scheduleItem);

        this.mvc.perform(post("/api/schedule").contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(scheduleItem)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("some-id")))
                .andExpect(jsonPath("$.triggerTime", is(scheduleItem.getTriggerTime().toString())));
    }

    @Test
    public void shouldGetScheduleItem() throws Exception {

        ScheduleItem scheduleItem = new ScheduleItem("some-id", Instant.now());

        given(this.schedulerService.getScheduleItem("some-id"))
                .willReturn(scheduleItem);

        this.mvc.perform(get("/api/schedule/{id}","some-id").contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("some-id")))
                .andExpect(jsonPath("$.triggerTime", is(scheduleItem.getTriggerTime().toString())));
    }


}
