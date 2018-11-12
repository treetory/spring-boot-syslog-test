package com.treetory.test.mvc.repository.test;

import org.springframework.transaction.annotation.Transactional;

public interface TestRepository {

    @Transactional
    void create();
}
