package dev.camilo.data.post;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Post(
  @Id
  Integer id,
  Integer userId,
  @NotEmpty( message = "The title is required")
  String title,
  @NotEmpty( message = "The body is required")
  String body,
  @Version
  Integer version
) {
}
