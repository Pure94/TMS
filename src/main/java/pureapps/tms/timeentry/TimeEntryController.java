package pureapps.tms.timeentry;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pureapps.tms.timeentry.dto.TimeEntryCreateDTO;
import pureapps.tms.timeentry.dto.TimeEntryDTO;
import pureapps.tms.timeentry.dto.TimeEntryUpdateDTO;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TimeEntryDTO> logTimeEntry(@Valid @RequestBody TimeEntryCreateDTO createDTO) {
        TimeEntryDTO createdEntry = timeEntryService.createTimeEntry(createDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdEntry.getId()).toUri();
        return ResponseEntity.created(location).body(createdEntry);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Page<TimeEntryDTO> getMyTimeEntries(Pageable pageable) {
        return timeEntryService.getMyTimeEntries(pageable);
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TimeEntryDTO getTimeEntryById(@PathVariable UUID id) {
        return timeEntryService.getTimeEntryById(id);
    }


    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TimeEntryDTO updateTimeEntry(@PathVariable UUID id, @Valid @RequestBody TimeEntryUpdateDTO updateDTO) {
        return timeEntryService.updateTimeEntry(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteTimeEntry(@PathVariable UUID id) {
        timeEntryService.deleteTimeEntry(id);
    }
}