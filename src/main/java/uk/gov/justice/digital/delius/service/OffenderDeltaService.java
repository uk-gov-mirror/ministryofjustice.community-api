package uk.gov.justice.digital.delius.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.digital.delius.controller.NotFoundException;
import uk.gov.justice.digital.delius.data.api.OffenderUpdate;
import uk.gov.justice.digital.delius.jpa.dao.OffenderDelta;
import uk.gov.justice.digital.delius.jpa.standard.repository.OffenderDeltaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class OffenderDeltaService {

    private final JdbcTemplate jdbcTemplate;
    private final OffenderDeltaRepository offenderDeltaRepository;
    @SuppressWarnings({"FieldCanBeLocal"})
    static final int IN_PROGRESS_IS_FAILED_AFTER_MINUTES = 10;
    /*
     * Last updated is used for optimistic locking which has a fidelity of 1 second, so we have to wait longer than
     * that to avoid clashes
     */
    static final int WAIT_BEFORE_LOCKING_DELTA_SECONDS = 2;

    public OffenderDeltaService(JdbcTemplate jdbcTemplate, OffenderDeltaRepository offenderDeltaRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.offenderDeltaRepository = offenderDeltaRepository;
    }

    public List<OffenderDelta> findAll() {
        return jdbcTemplate.query("SELECT * FROM OFFENDER_DELTA", (resultSet, rowNum) ->
                OffenderDelta.builder()
                        .offenderId(resultSet.getLong("OFFENDER_ID"))
                        .dateChanged(resultSet.getTimestamp("DATE_CHANGED").toLocalDateTime())
                        .action(resultSet.getString("ACTION"))
                        .build())
                .stream()
                .limit(1000) // limit to 1000 without using database specific syntax specific technology
                .collect(toList());
    }

    @Transactional
    public void deleteBefore(LocalDateTime dateTime) {
        jdbcTemplate.update("DELETE FROM OFFENDER_DELTA WHERE DATE_CHANGED < ?", dateTime);
    }

    @Transactional
    public Optional<OffenderUpdate> lockNextUpdate() {
        final var createdCutOffTime = getCreatedCutOffTime();
        final var mayBeDelta = offenderDeltaRepository.findFirstByStatusAndLastUpdatedDateTimeLessThanEqualOrderByCreatedDateTime("CREATED", createdCutOffTime);

        mayBeDelta.ifPresent(delta -> offenderDeltaRepository.deleteOtherDuplicates(delta.getOffenderDeltaId()));
        return transformAndLock(mayBeDelta);
    }

    private LocalDateTime getCreatedCutOffTime() {
        return LocalDateTime.now().minusSeconds(WAIT_BEFORE_LOCKING_DELTA_SECONDS);
    }

    @Transactional
    public Optional<OffenderUpdate> lockNextFailedUpdate() {
        final var failedCutoffDateTime = getFailedCutoffDateTime();
        final var mayBeDelta = offenderDeltaRepository.findFirstByStatusAndLastUpdatedDateTimeLessThanEqualOrderByCreatedDateTime("INPROGRESS", failedCutoffDateTime);

        return transformAndLock(mayBeDelta).map(OffenderUpdate::setAsFailed);
    }

    private LocalDateTime getFailedCutoffDateTime() {
        return LocalDateTime.now().minusMinutes(IN_PROGRESS_IS_FAILED_AFTER_MINUTES);
    }

    @NotNull
    private Optional<OffenderUpdate> transformAndLock(final Optional<uk.gov.justice.digital.delius.jpa.standard.entity.OffenderDelta> mayBeDelta) {
        return mayBeDelta
                .map(uk.gov.justice.digital.delius.jpa.standard.entity.OffenderDelta::setInProgress)
                .map(this::transformDelta);
    }

    private OffenderUpdate transformDelta(final uk.gov.justice.digital.delius.jpa.standard.entity.OffenderDelta delta) {
        return OffenderUpdate.builder()
                .offenderDeltaId(delta.getOffenderDeltaId())
                .offenderId(delta.getOffenderId())
                .dateChanged(delta.getDateChanged())
                .action(delta.getAction())
                .sourceTable(delta.getSourceTable())
                .sourceRecordId(delta.getSourceRecordId())
                .status(delta.getStatus())
                .build();
    }

    public void deleteDelta(final Long offenderDeltaId) {
        offenderDeltaRepository.deleteById(offenderDeltaId);
    }

    public void markAsFailed(final Long offenderDeltaId) {
        offenderDeltaRepository.findById(offenderDeltaId)
                .orElseThrow(() -> new NotFoundException(format("Cannot mark as failed for offenderDeltaId %s", offenderDeltaId)))
                .markAsFailed();
    }
}
