package io.github.clamentos.blackhole.scaffolding;

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import java.util.List;

public interface Projectable extends Reducible {
    
    List<DataEntry> project(long fields);
}
