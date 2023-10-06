package dev.camilo.data.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PostDataLoader implements CommandLineRunner {

  //logger sl4j
  private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
  private final ObjectMapper objectMapper;
  private final PostRepository postRepository;

  public PostDataLoader(ObjectMapper objectMapper, PostRepository postRepository) {
    this.objectMapper = objectMapper;
    this.postRepository = postRepository;
  }

  // loading data from JSON file into database on startup
  @Override
  public void run(String... args) throws Exception {
    if(postRepository.count() == 0) {
      String POST_JSON = "/data/posts.json";
      log.info("Loading posts into database from JSON {}", POST_JSON);
      try(InputStream inputStream = TypeReference.class.getResourceAsStream(POST_JSON)) {
        Posts response = objectMapper.readValue(inputStream, Posts.class);
        postRepository.saveAll(response.posts());
      }catch (IOException e){
        throw new RuntimeException("Failed to read JSON data ", e);
      }

    }
  }
}
