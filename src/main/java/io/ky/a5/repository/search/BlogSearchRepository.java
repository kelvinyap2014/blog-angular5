package io.ky.a5.repository.search;

import io.ky.a5.domain.Blog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Blog entity.
 */
public interface BlogSearchRepository extends ElasticsearchRepository<Blog, Long> {
}
