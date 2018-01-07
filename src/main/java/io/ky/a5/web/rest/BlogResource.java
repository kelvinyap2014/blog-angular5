package io.ky.a5.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import io.ky.a5.domain.Blog;
import io.ky.a5.repository.BlogRepository;
import io.ky.a5.repository.search.BlogSearchRepository;
import io.ky.a5.web.rest.errors.BadRequestAlertException;
import io.ky.a5.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Blog.
 */
@RestController
@RequestMapping("/api")
public class BlogResource {

    private final Logger log = LoggerFactory.getLogger(BlogResource.class);

    private static final String ENTITY_NAME = "blog";

    private final BlogRepository blogRepository;

    private final BlogSearchRepository blogSearchRepository;

    public BlogResource(final BlogRepository blogRepository, final BlogSearchRepository blogSearchRepository) {
        this.blogRepository = blogRepository;
        this.blogSearchRepository = blogSearchRepository;
    }

    /**
     * POST  /blogs : Create a new blog.
     *
     * @param blog the blog to create
     * @return the ResponseEntity with status 201 (Created) and with body the new blog, or with status 400 (Bad Request) if the blog has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/blogs")
    @Timed
    public ResponseEntity<Blog> createBlog(@Valid @RequestBody final Blog blog) throws URISyntaxException {
        this.log.debug("REST request to save Blog : {}", blog);
        if (blog.getId() != null) {
            throw new BadRequestAlertException("A new blog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        final Blog result = this.blogRepository.save(blog);
        this.blogSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/blogs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /blogs : Updates an existing blog.
     *
     * @param blog the blog to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated blog,
     * or with status 400 (Bad Request) if the blog is not valid,
     * or with status 500 (Internal Server Error) if the blog couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/blogs")
    @Timed
    public ResponseEntity<Blog> updateBlog(@Valid @RequestBody final Blog blog) throws URISyntaxException {
        this.log.debug("REST request to update Blog : {}", blog);
        if (blog.getId() == null) {
            return createBlog(blog);
        }
        final Blog result = this.blogRepository.save(blog);
        this.blogSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, blog.getId().toString()))
            .body(result);
    }

    /**
     * GET  /blogs : get all the blogs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of blogs in body
     */
    @GetMapping("/blogs")
    @Timed
    public List<Blog> getAllBlogs() {
        this.log.debug("REST request to get all Blogs");
		return this.blogRepository.findByUserIsCurrentUser();
        }

    /**
     * GET  /blogs/:id : get the "id" blog.
     *
     * @param id the id of the blog to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the blog, or with status 404 (Not Found)
     */
    @GetMapping("/blogs/{id}")
    @Timed
    public ResponseEntity<Blog> getBlog(@PathVariable final Long id) {
        this.log.debug("REST request to get Blog : {}", id);
        final Blog blog = this.blogRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(blog));
    }

    /**
     * DELETE  /blogs/:id : delete the "id" blog.
     *
     * @param id the id of the blog to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/blogs/{id}")
    @Timed
    public ResponseEntity<Void> deleteBlog(@PathVariable final Long id) {
        this.log.debug("REST request to delete Blog : {}", id);
        this.blogRepository.delete(id);
        this.blogSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/blogs?query=:query : search for the blog corresponding
     * to the query.
     *
     * @param query the query of the blog search
     * @return the result of the search
     */
    @GetMapping("/_search/blogs")
    @Timed
    public List<Blog> searchBlogs(@RequestParam final String query) {
        this.log.debug("REST request to search Blogs for query {}", query);
        return StreamSupport
            .stream(this.blogSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
