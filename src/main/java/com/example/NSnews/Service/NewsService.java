package com.example.NSnews.Service;

import com.example.NSnews.Entry.News;
import com.example.NSnews.Repository.NewsRepo;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class NewsService {

    @Autowired
    private NewsRepo repo;

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;

    public List<News> getAllNews() {
        return repo.findAll();
    }

    public void addNews(News news, MultipartFile image)throws IOException {
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType());
        news.setImage(imageId.toString());
        news.setTime(LocalDateTime.now());
        repo.save(news);
    }

    public void addNews(News news) {
        news.setTime(LocalDateTime.now());
        repo.save(news);
    }
    public GridFSFile getImage(ObjectId newsId) {
        News news = repo.findNewsById(newsId);
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(news.getImage())));
    }

    public News getNewsById(ObjectId id) {
        return repo.findNewsById(id);
    }

    public boolean deleteNewsById(ObjectId id) {
        News news = repo.findNewsById(id);
        if(news == null) return false;
        repo.delete(news);
        return true;
    }

    public List<News> getLatestNews(int page, int size) {
        Page<News> newsPage = repo.findAllByOrderByTimeDesc(PageRequest.of(page, size));
        return newsPage.getContent();
    }







    public byte[] getImage(String imageId) throws IOException {
        // Convert the string ID to ObjectId
        ObjectId objectId = new ObjectId(imageId);

        // Retrieve the GridFS file from MongoDB by its ObjectId
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)));

        if (gridFSFile != null) {
            // Retrieve the file content as an InputStream
            try (GridFSDownloadStream downloadStream = (GridFSDownloadStream) gridFsTemplate.getResource(gridFSFile).getInputStream()) {
                // Convert InputStream to byte array
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = downloadStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return byteArrayOutputStream.toByteArray();
            }
        }
        return null;
    }

}
