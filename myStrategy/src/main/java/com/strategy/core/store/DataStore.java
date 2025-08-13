package com.strategy.core.store;

import java.util.Map;

/**
 * 2023-4-25
 */
public interface DataStore {


    enum TableModel {
        EXISTS,
        AUTO_CREATE

    }


    DataStore table(String name, TableModel model);

    DataStore primary(String key);

    DataStore batchSize(int size);

    DataStore upsert(String condition);

    DataStore execute();

    DataStore mapping(Map<String, String> map);

}
