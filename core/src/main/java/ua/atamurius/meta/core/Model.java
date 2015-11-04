package ua.atamurius.meta.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Meta Types Model.
 * Created by atamurius on 04.11.15.
 */
public class Model {

    private final AtomicLong idSeq = new AtomicLong();

    final Map<String,Type> typesByName = new HashMap<>();
    final Map<Long,Type> types = new HashMap<>();
    final Map<Long,Attribute> attributes = new HashMap<>();

    public Type createType() {
        Type type = new Type(this);
        types.put(type.getId(), type);
        return type;
    }

    long nextId() {
        return idSeq.incrementAndGet();
    }
}
