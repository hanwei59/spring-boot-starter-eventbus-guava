package com.github.hanwei59.eventbus;

import lombok.Data;

import java.util.Random;

@Data
public abstract class BaseEvent {
    private static final Random RANDOM = new Random();

    private Long id;

    public BaseEvent() {
        long l = (long) (1000 * RANDOM.nextDouble());
        long eventId = System.currentTimeMillis() * 10000 + l;
        this.id = eventId;
    }

}
