package io.ky.a5.repository;

import io.ky.a5.domain.Blog;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Blog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("select blog from Blog blog where blog.user.login = ?#{principal.username}")
    List<Blog> findByUserIsCurrentUser();

}
