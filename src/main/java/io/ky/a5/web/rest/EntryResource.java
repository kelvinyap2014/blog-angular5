package io.ky.a5.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import io.ky.a5.domain.Entry;
import io.ky.a5.repository.EntryRepository;
import io.ky.a5.repository.search.EntrySearchRepository;
import io.ky.a5.security.SecurityUtils;
import io.ky.a5.web.rest.errors.BadRequestAlertException;
import io.ky.a5.web.rest.util.HeaderUtil;
import io.ky.a5.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Entry.
 */
@RestController
@RequestMapping("/api")
public class EntryResource {

    private final Logger log = LoggerFactory.getLogger(EntryResource.class);

    private static final String ENTITY_NAME = "entry";

    private final EntryRepository entryRepository;

    private final EntrySearchRepository entrySearchRepository;

    public EntryResource(final EntryRepository entryRepository, final EntrySearchRepository entrySearchRepository) {
        this.entryRepository = entryRepository;
        this.entrySearchRepository = entrySearchRepository;
    }

    /**
     * POST  /entries : Create a new entry.
     *
     * @param entry the entry to create
     * @return the ResponseEntity with status 201 (Created) and with body the new entry, or with status 400 (Bad Request) if the entry has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/entries")
    @Timed
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody final Entry entry) throws URISyntaxException {
        this.log.debug("REST request to save Entry : {}", entry);
        if (entry.getId() != null) {
            throw new BadRequestAlertException("A new entry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        final Entry result = this.entryRepository.save(entry);
        this.entrySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /entries : Updates an existing entry.
     *
     * @param entry the entry to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated entry,
     * or with status 400 (Bad Request) if the entry is not valid,
     * or with status 500 (Internal Server Error) if the entry couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/entries")
    @Timed
    public ResponseEntity<Entry> updateEntry(@Valid @RequestBody final Entry entry) throws URISyntaxException {
        this.log.debug("REST request to update Entry : {}", entry);
        if (entry.getId() == null) {
            return createEntry(entry);
        }
        final Entry result = this.entryRepository.save(entry);
        this.entrySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, entry.getId().toString()))
            .body(result);
    }

    /**
     * GET  /entries : get all the entries.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of entries in body
     */
    @GetMapping("/entries")
    @Timed
    public ResponseEntity<List<Entry>> getAllEntries(final Pageable pageable) {
        this.log.debug("REST request to get a page of Entries");
		final Page<Entry> page = this.entryRepository.findByBlogUserLoginOrderByDateDesc(SecurityUtils.getCurrentUserLogin(), pageable);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/entries");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /entries/:id : get the "id" entry.
     *
     * @param id the id of the entry to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the entry, or with status 404 (Not Found)
     */
    @GetMapping("/entries/{id}")
    @Timed
    public ResponseEntity<Entry> getEntry(@PathVariable final Long id) {
        this.log.debug("REST request to get Entry : {}", id);
        final Entry entry = this.entryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(entry));
    }

    /**
     * DELETE  /entries/:id : delete the "id" entry.
     *
     * @param id the id of the entry to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/entries/{id}")
    @Timed
    public ResponseEntity<Void> deleteEntry(@PathVariable final Long id) {
        this.log.debug("REST request to delete Entry : {}", id);
        this.entryRepository.delete(id);
        this.entrySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/entries?query=:query : search for the entry corresponding
     * to the query.
     *
     * @param query the query of the entry search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/entries")
    @Timed
    public ResponseEntity<List<Entry>> searchEntries(@RequestParam final String query, final Pageable pageable) {
        this.log.debug("REST request to search for a page of Entries for query {}", query);
        final Page<Entry> page = this.entrySearchRepository.search(queryStringQuery(query), pageable);
        final HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/entries");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
