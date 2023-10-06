package dev.camilo.data.post;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostRepository postRepository;

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @GetMapping
  List<Post> findAll() {
    return postRepository.findAll();
  }

  @GetMapping("/{id}")
  Optional<Post> findById(@PathVariable Integer id) {
    return Optional.ofNullable(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Post create(@RequestBody @Valid Post post) {
    return postRepository.save(post);
  }

  @PutMapping("/{id}")
  Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
    return postRepository.findById(id).map(p -> {
      Post NewPostUpdated = new Post(
          p.id(),
          p.userId(),
          post.title(),
          post.body(),
          p.version()
      );
      return postRepository.save(NewPostUpdated);
    }).orElseThrow(PostNotFoundException::new);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable Integer id) {
    postRepository.deleteById(id);
  }

}