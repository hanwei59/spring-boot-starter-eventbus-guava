package com.github.hanwei59.eventbus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestEvent extends BaseEvent{
    private String text;
}
