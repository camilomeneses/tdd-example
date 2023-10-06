package dev.camilo.data;

import dev.camilo.data.post.Post;
import dev.camilo.data.post.PostController;
import dev.camilo.data.post.PostNotFoundException;
import dev.camilo.data.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

  @Autowired
  MockMvc mockMvc; // for REST API

  List<Post> posts = new ArrayList<>(); // mock of post

  @MockBean
  PostRepository postRepository; // for DB

  @BeforeEach
  void setUp() {
    // create some posts
    posts = List.of(
        new Post(1, 1, "Hello, World!", "This is my first post.", null),
        new Post(2, 1, "Second Post", "This is my second post.", null)
    );
  }

  // test all post /api/posts
  @Test
  void shouldFindAllPosts() throws Exception {
    String jsonResponse = """
        [
            {
                "id":1,
                "userId":1,
                "title":"Hello, World!",
                "body":"This is my first post.",
                "version": null
            },
            {
                "id":2,
                "userId":1,
                "title":"Second Post",
                "body":"This is my second post.",
                "version": null
            }
        ]
        """;

    when(postRepository.findAll()).thenReturn(posts); // mock the DB

    mockMvc.perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));
  }

  // test post /api/posts/{id}
  @Test
  void shouldFindPostWhenGivenValidID() throws Exception {
    when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0))); // mock the DB

    String json = """
        {
            "id":1,
            "userId":1,
            "title":"Hello, World!",
            "body":"This is my first post.",
            "version": null
        }
        """;

    mockMvc.perform(get("/api/posts/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }


  // test post /api/posts/{invalidID}
  @Test
  void shouldNotFindPostWhenGivenInvalidID() throws Exception {
    when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

    mockMvc.perform(get("/api/posts/999"))
        .andExpect(status().isNotFound());
  }

  // new post /api/posts
  @Test
  void shouldCreatePostWhenGivenValidPost() throws Exception {
    Post post = new Post(3, 1, "Third Post", "This is my third post.", null);

    when(postRepository.save(post)).thenReturn(post); // mock the DB

    String json = """
        {
            "id":3,
            "userId":1,
            "title":"Third Post",
            "body":"This is my third post.",
            "version": null
        }
        """;

    mockMvc.perform(post("/api/posts")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().json(json)); // check if the response is the same as the request
  }

  // invalid post /api/posts
  @Test
  void shouldNotCreatePostWhenGivenInvalidPost() throws Exception {
    Post post = new Post(3, 1, "", "", null);

    when(postRepository.save(post)).thenReturn(post); // mock DB

    String json = """
        {
            "id":3,
            "userId":1,
            "title":"",
            "body":"",
            "version": null
        }
        """;

    mockMvc.perform(post("/api/posts")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isBadRequest());

  }

  // update post /api/posts/{id}
  @Test
  void shouldUpdatePostWhenGivenValidPost() throws Exception {
    Post updatedPost = new Post(1, 1, "Updated Post", "This is my updated post.", null);
    when(postRepository.findById(1)).thenReturn(Optional.of(updatedPost));
    when(postRepository.save(updatedPost)).thenReturn(updatedPost); // mock DB
    String json = """
        {
            "id":1,
            "userId":1,
            "title":"Updated Post",
            "body":"This is my updated post.",
            "version": null
        }
        """;
    mockMvc.perform(put("/api/posts/1")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  // not update post /api/posts/{invalidID}
  @Test
  void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
    Post updatedPost = new Post(1, 1, "Updated Post", "This is my updated post.", null);

    when(postRepository.save(updatedPost)).thenReturn(updatedPost); // mock DB

    String json = """
        {
            "id":1,
            "userId":1,
            "title":"Updated Post",
            "body":"This is my updated post.",
            "version": null
        }
        """;

    mockMvc.perform(put("/api/posts/999")
            .contentType("application/json")
            .content(json))
        .andExpect(status().isNotFound());
  }

  // delete post /api/posts/{id}
  @Test
  void shouldDeletePostWhenGivenValidID() throws Exception {
    doNothing().when(postRepository).deleteById(1); // mock DB

    mockMvc.perform(delete("/api/posts/1"))
        .andExpect(status().isNoContent()); // check if the response is the same as the request
    verify(postRepository, times(1)).deleteById(1); // check if the method was called once with the given ID

  }

}
