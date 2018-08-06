package com.myapp.voiceapp.functional;

import com.myapp.voiceapp.models.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.myapp.voiceapp.repositories.QueryRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractEventBaseFunctionalTest extends AbstractBaseFunctionalTest {

    @Autowired
    QueryRepository queryRepository;

    protected Query createAndSaveSingleEvent() {
        Date eventDate = new Date();
        Query newQuery = new Query("The Title", "option_2", "option_1",eventDate, "The Location");
        queryRepository.save(newQuery);
        return newQuery;
    }

    protected List<Query> createAndSaveMultipleEvents(int numberOfEvents) {
        List<Query> queries = new ArrayList<>();
        for (int i=0; i<numberOfEvents; i++) {
            Query e = new Query("Query "+Integer.toString(i), "option_1", "option_2",new Date());
            queryRepository.save(e);
            queries.add(e);
        }
        return queries;
    }

}
