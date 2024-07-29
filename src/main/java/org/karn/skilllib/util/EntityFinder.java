package org.karn.skilllib.util;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFinder {
    public static <T extends Entity, R extends Entity> List<T> filter(Collection<R> entities, Class<T> targetToParse) {
        if ( !Entity.class.isAssignableFrom(targetToParse) ) {
            return new ArrayList<>(0);
        }
        return entities.stream().filter(targetToParse::isInstance)
                .map(entity -> (T) entity)
                .collect(Collectors.toList());
    }
}
