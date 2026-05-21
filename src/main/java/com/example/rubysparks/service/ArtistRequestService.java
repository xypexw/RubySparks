package com.example.rubysparks.service;

import com.example.rubysparks.dto.ArtistRequestDTO;
import com.example.rubysparks.model.ArtistRequest;
import com.example.rubysparks.model.User;
import com.example.rubysparks.repository.ArtistRequestRepository;
import com.example.rubysparks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistRequestService {

    private final ArtistRequestRepository artistRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArtistRequestDTO submitRequest(ArtistRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getStageName() == null || dto.getStageName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nghệ danh (Stage Name) không được để trống.");
        }

        String trimmedStageName = dto.getStageName().trim();
        if (userRepository.existsByStageNameIgnoreCase(trimmedStageName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nghệ danh (Stage Name) đã được sử dụng bởi nghệ sĩ khác.");
        }

        ArtistRequest request = ArtistRequest.builder()
                .user(user)
                .stageName(trimmedStageName)
                .genre(dto.getGenre())
                .bio(dto.getBio())
                .status("PENDING")
                .build();

        ArtistRequest savedRequest = artistRequestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    public List<ArtistRequestDTO> getAllRequests(String status) {
        List<ArtistRequest> requests = (status != null && !status.isEmpty())
                ? artistRequestRepository.findByStatus(status)
                : artistRequestRepository.findAll();

        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ArtistRequestDTO> getUserRequests(UUID userId) {
        return artistRequestRepository.findByUserUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ArtistRequestDTO updateRequestStatus(UUID requestId, String status) {
        ArtistRequest request = artistRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Artist request not found"));

        request.setStatus(status);
        ArtistRequest savedRequest = artistRequestRepository.save(request);

        if ("APPROVED".equalsIgnoreCase(status)) {
            User user = request.getUser();
            String stageName = request.getStageName();
            if (stageName == null || stageName.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu nghệ sĩ không hợp lệ: Thiếu nghệ danh.");
            }
            String trimmedStageName = stageName.trim();
            if (userRepository.existsByStageNameIgnoreCaseAndUserIdNot(trimmedStageName, user.getUserId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nghệ danh (Stage Name) đã được sử dụng bởi nghệ sĩ khác.");
            }
            user.setRole("ARTIST");
            user.setStageName(trimmedStageName);
            userRepository.save(user);
        }

        return convertToDTO(savedRequest);
    }

    public ArtistRequestDTO convertToDTO(ArtistRequest request) {
        User user = request.getUser();
        return ArtistRequestDTO.builder()
                .requestId(request.getRequestId())
                .userId(user.getUserId())
                .userName(user.getStageName() != null && !user.getStageName().isEmpty() ? user.getStageName() : user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .stageName(request.getStageName())
                .genre(request.getGenre())
                .bio(request.getBio())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
