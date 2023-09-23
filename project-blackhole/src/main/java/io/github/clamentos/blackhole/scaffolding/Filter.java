package io.github.clamentos.blackhole.scaffolding;

public interface Filter {
    
    // Checks if this "filters" the entity
    boolean isFiltered(Entity entity);
}
