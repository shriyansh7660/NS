package com.example.NSnews.Controller;

import com.example.NSnews.Entry.News;
import com.example.NSnews.Service.NewsService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllNews(){
        return new ResponseEntity<>(newsService.getAllNews(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<News> addNews(@RequestBody News news){
        newsService.addNews(news);
        return new ResponseEntity<>(news, HttpStatus.CREATED);
    }

    @PostMapping("/img/{id}")
    public ResponseEntity<News> addImage(@PathVariable ObjectId id,
                                         @RequestParam("image") MultipartFile image) throws IOException {
        newsService.addNews(newsService.getNewsById(id), image);
        return new ResponseEntity<>(newsService.getNewsById(id),HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable ObjectId id){
        News news = newsService.getNewsById(id);
        return new ResponseEntity<>(news,HttpStatus.FOUND);
    }
    @DeleteMapping("/{id}")
    public boolean deleteNews(@PathVariable ObjectId id){
        return newsService.deleteNewsById(id);
    }

    @GetMapping
    public List<News> getLatestNews(@RequestParam(defaultValue = "0") int page) {
        return newsService.getLatestNews(page, 5);
    }

    @GetMapping("/deleteAll")
    public boolean deleteAll(){
        List<News> news = newsService.getAllNews();
        for(News n : news){
            newsService.deleteNewsById(n.getId());
        }
        return true;
    }


    @GetMapping("/news/image/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        try {
            byte[] image = newsService.getImage(imageId);
            if (image != null) {
                return ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")  // Adjust based on your image type (JPEG, PNG, etc.)
                        .body(image);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

}
