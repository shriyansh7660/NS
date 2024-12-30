package com.example.NSnews.Repository;

import com.example.NSnews.Entry.News;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsRepo extends MongoRepository<News, ObjectId> {
    News findNewsById(ObjectId id);
    Page<News> findAllByOrderByTimeDesc(PageRequest pageable);
}

