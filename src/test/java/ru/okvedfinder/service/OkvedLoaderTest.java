package ru.okvedfinder.service;

import org.junit.jupiter.api.Test;
import ru.okvedfinder.domain.OkvedEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OkvedLoaderTest {

    @Test
    void load() throws Exception {
        List<OkvedEntry> okvedList = OkvedLoader.load();
        assertNotNull(okvedList);
        assertFalse(okvedList.isEmpty());
        for (OkvedEntry okvedEntry : okvedList) {assertNotNull(okvedEntry);}
    }
}