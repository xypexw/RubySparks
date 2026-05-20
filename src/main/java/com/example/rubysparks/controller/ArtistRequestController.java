package com.example.rubysparks.controller;

import com.example.rubysparks.dto.ArtistRequestDTO;
import com.example.rubysparks.service.ArtistRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artist-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArtistRequestController {

    private final ArtistRequestService artistRequestService;

    @PostMapping
    public ResponseEntity<ArtistRequestDTO> submitRequest(@RequestBody ArtistRequestDTO dto) {
        return ResponseEntity.ok(artistRequestService.submitRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<ArtistRequestDTO>> getAllRequests(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(artistRequestService.getAllRequests(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ArtistRequestDTO>> getUserRequests(@PathVariable UUID userId) {
        return ResponseEntity.ok(artistRequestService.getUserRequests(userId));
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<ArtistRequestDTO> updateRequestStatus(
            @PathVariable UUID requestId,
            @RequestParam String status) {
        return ResponseEntity.ok(artistRequestService.updateRequestStatus(requestId, status));
    }
}
