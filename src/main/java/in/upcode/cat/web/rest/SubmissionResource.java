package in.upcode.cat.web.rest;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import in.upcode.cat.domain.Assessment;
import in.upcode.cat.domain.Submission;
import in.upcode.cat.domain.User;
import in.upcode.cat.domain.UserAssessment;
import in.upcode.cat.repository.AssessmentRepository;
import in.upcode.cat.repository.SubmissionRepository;
import in.upcode.cat.repository.UserAssessmentRepository;
import in.upcode.cat.repository.UserRepository;
import in.upcode.cat.service.SubmissionService;
import in.upcode.cat.service.UserService;
import in.upcode.cat.service.dto.SubmissionDTO;
import in.upcode.cat.service.dto.UserDTO;
import in.upcode.cat.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link in.upcode.cat.domain.Submission}.
 */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionResource {

    private final Logger log = LoggerFactory.getLogger(SubmissionResource.class);

    private static final String ENTITY_NAME = "submission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubmissionService submissionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    public SubmissionResource(
        SubmissionService submissionService,
        SubmissionRepository submissionRepository,
        UserRepository userRepository,
        AssessmentRepository assessmentRepository
    ) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
    }

    /**
     * {@code POST  /submissions} : Create a new submission.
     *
     * @param submissionDTO the submissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new submissionDTO, or with status {@code 400 (Bad Request)} if the submission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubmissionDTO> createSubmission(@Valid @RequestBody SubmissionDTO submissionDTO) throws URISyntaxException {
        log.debug("REST request to save Submission : {}", submissionDTO);
        if (submissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new submission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubmissionDTO result = submissionService.save(submissionDTO);
        return ResponseEntity
            .created(new URI("/api/submissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code POST  /submissions/submit} : Create a new submission.
     *
     * @param submissionDTO the submissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new submissionDTO, or with status {@code 400 (Bad Request)} if the submission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/submit")
    public ResponseEntity<SubmissionDTO> createSubmissionSubmit(@Valid @RequestBody SubmissionDTO submissionDTO)
        throws URISyntaxException, IOException, InterruptedException, CheckstyleException {
        log.debug("REST request to save Submission : {}", submissionDTO);
        if (submissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new submission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        final SubmissionDTO result = submissionService.saveSubmit(submissionDTO);
        return ResponseEntity
            .created(new URI("/api/submissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code POST  /submissions/submit} : Create a new submission.
     *
     * @param submissionDTO the submissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new submissionDTO, or with status {@code 400 (Bad Request)} if the submission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/check")
    public ResponseEntity<SubmissionDTO> createSubmissionGithubRepo(@Valid @RequestBody SubmissionDTO submissionDTO) throws Exception {
        log.debug("REST request to check Quality Submission : {}", submissionDTO);
        if (submissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new submission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        final SubmissionDTO result = submissionService.checkQuality(submissionDTO);
        return ResponseEntity
            .created(new URI("/api/submissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /submissions/:id} : Updates an existing submission.
     *
     * @param id the id of the submissionDTO to save.
     * @param submissionDTO the submissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated submissionDTO,
     * or with status {@code 400 (Bad Request)} if the submissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the submissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubmissionDTO> updateSubmission(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody SubmissionDTO submissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Submission : {}, {}", id, submissionDTO);
        if (submissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, submissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!submissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubmissionDTO result = submissionService.update(submissionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, submissionDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /submissions/:id} : Partial updates given fields of an existing submission, field will ignore if it is null
     *
     * @param id the id of the submissionDTO to save.
     * @param submissionDTO the submissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated submissionDTO,
     * or with status {@code 400 (Bad Request)} if the submissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the submissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the submissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubmissionDTO> partialUpdateSubmission(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody SubmissionDTO submissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Submission partially : {}, {}", id, submissionDTO);
        if (submissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, submissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!submissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubmissionDTO> result = submissionService.partialUpdate(submissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, submissionDTO.getId())
        );
    }

    /**
     * {@code GET  /submissions} : get all the submissions.
     *
     * @param pageable the pagination information.
     * @param user the students name
     * @param type the assessment type
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of submissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions(
        @RequestParam(required = false) String user,
        @RequestParam(required = false) String type,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of Submissions based on search");

        Page<SubmissionDTO> page;
        HttpHeaders headers;

        if (user != null && type != null) {
            // Case: Both user and type parameters are provided
            Optional<User> userOptional = userRepository.findOneByLoginRegexIgnoreCase(user);
            Optional<Assessment> assessmentOptional = assessmentRepository.findByTypeRegexIgnoreCase(type);

            if (userOptional.isPresent() && assessmentOptional.isPresent()) {
                String userId = userOptional.get().getId();
                String assessmentId = assessmentOptional.get().getId();
                page = submissionService.findByUserIdAndAssessmentId(userId, assessmentId, pageable);
            } else {
                // If either user or assessment is not found, return an empty list
                return ResponseEntity.ok().body(Collections.emptyList());
            }
        } else if (user != null) {
            // Case: Only user parameter is provided
            Optional<User> userOptional = userRepository.findOneByLoginRegexIgnoreCase(user);
            if (userOptional.isPresent()) {
                String userId = userOptional.get().getId();
                page = submissionService.findByUserId(userId, pageable);
            } else {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
        } else if (type != null) {
            // Case: Only type parameter is provided
            Optional<Assessment> assessmentOptional = assessmentRepository.findByTypeRegexIgnoreCase(type);
            if (assessmentOptional.isPresent()) {
                String assessmentId = assessmentOptional.get().getId();
                page = submissionService.findByAssessmentId(assessmentId, pageable);
            } else {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
        } else {
            // Case: No parameters provided, return all submissions
            page = submissionService.findAll(pageable);
        }
        headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /submissions/search} : get all the submissions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of submissions in body.
     */
    @GetMapping("/search")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsBySearch(
        @RequestParam(required = false) String user,
        @RequestParam(required = false) String type,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of Submissions based on search");

        final User userOptional;
        final String userId;

        final Assessment assessmentOptional;
        final String assessmentId;

        if (user != null) {
            Optional<User> userName = userRepository.findOneByLoginRegexIgnoreCase(user);
            if (userName.isPresent()) {
                userOptional = userName.get();
                userId = userOptional.getId();

                Page<SubmissionDTO> page = submissionService.findByUserId(userId, pageable);
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            } else {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
        } else if (type != null) {
            //get the id of the assessment based on search
            Optional<Assessment> assessmentName = assessmentRepository.findByTypeRegexIgnoreCase(type);
            if (assessmentName.isPresent()) {
                assessmentOptional = assessmentName.get();
                assessmentId = assessmentOptional.getId();

                Page<SubmissionDTO> page = submissionService.findByAssessmentId(assessmentId, pageable);
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            } else {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
        } else {
            // Return an empty list
            return ResponseEntity.ok().body(Collections.emptyList());
        }
    }

    /**
     * {@code GET  /submissions/:id} : get the "id" submission.
     *
     * @param id the id of the submissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the submissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable("id") String id) {
        log.debug("REST request to get Submission : {}", id);
        Optional<SubmissionDTO> submissionDTO = submissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(submissionDTO);
    }

    /**
     * {@code DELETE  /submissions/:id} : delete the "id" submission.
     *
     * @param id the id of the submissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable("id") String id) {
        log.debug("REST request to delete Submission : {}", id);
        submissionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
