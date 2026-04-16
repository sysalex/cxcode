package com.cxcode.exam.infrastructure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mysql")
public class MysqlSeedDataInitializer implements ApplicationRunner {
    private final MysqlExamStore store;
    private final SeedData seedData;

    public MysqlSeedDataInitializer(MysqlExamStore store, SeedData seedData) {
        this.store = store;
        this.seedData = seedData;
    }

    @Override
    public void run(ApplicationArguments args) {
        store.seedIfEmpty(seedData);
    }
}
