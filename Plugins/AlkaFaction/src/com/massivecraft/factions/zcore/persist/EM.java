package com.massivecraft.factions.zcore.persist;

import java.util.LinkedHashMap;
import java.util.Map;

public class EM {
    public static Map<Class<? extends Entity>, EntityCollection<? extends Entity>> class2Entities = new LinkedHashMap<Class<? extends Entity>, EntityCollection<? extends Entity>>();

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityCollection<T> getEntitiesCollectionForEntityClass(final Class<T> entityClass) {
        return (EntityCollection<T>) EM.class2Entities.get(entityClass);
    }

    public static void setEntitiesCollectionForEntityClass(final Class<? extends Entity> entityClass, final EntityCollection<? extends Entity> entities) {
        EM.class2Entities.put(entityClass, entities);
    }

    // -------------------------------------------- //
    // ATTACH AND DETACH
    // -------------------------------------------- //

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void attach(final T entity) {
        final EntityCollection<T> ec = (EntityCollection<T>) EM.getEntitiesCollectionForEntityClass(entity.getClass());
        ec.attach(entity);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> void detach(final T entity) {
        final EntityCollection<T> ec = (EntityCollection<T>) EM.getEntitiesCollectionForEntityClass(entity.getClass());
        ec.detach(entity);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> boolean attached(final T entity) {
        final EntityCollection<T> ec = (EntityCollection<T>) EM.getEntitiesCollectionForEntityClass(entity.getClass());
        return ec.attached(entity);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> boolean detached(final T entity) {
        final EntityCollection<T> ec = (EntityCollection<T>) EM.getEntitiesCollectionForEntityClass(entity.getClass());
        return ec.detached(entity);
    }

    // -------------------------------------------- //
    // DISC
    // -------------------------------------------- //

    public static void saveAllToDisc() {
        for (final EntityCollection<? extends Entity> ec : EM.class2Entities.values()) {
            ec.saveToDisc();
        }
    }

    public static void loadAllFromDisc() {
        for (final EntityCollection<? extends Entity> ec : EM.class2Entities.values()) {
            ec.loadFromDisc();
        }
    }
}
