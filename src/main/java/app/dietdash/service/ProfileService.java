package app.dietdash.service;

import app.dietdash.dto.ProfileDto;
import app.dietdash.dto.UserDto;
import app.dietdash.model.Profile;
import app.dietdash.model.User;
import app.dietdash.repository.ProfileRepository;
import app.dietdash.service.mapper.UserMapper;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UtilService utilService;
    private final UserMapper mapper;

    public List<ProfileDto> getAllProfiles(final String term, final UserDto principal) {
        return utilService.getUserFromPrincipal(principal).getProfileList()
                .stream()
                .filter(x -> term == null || x.getName().toLowerCase().contains(term.toLowerCase()))
                .map(mapper::toProfileDto)
                .toList();
    }

    public Profile getProfileById(final UUID id) {
        return profileRepository.findById(id).orElseThrow(() -> new AppException("Unknown profile with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UUID addProfile(final Profile profile, final UserDto principal) {
        final User user = utilService.getUserFromPrincipal(principal);

        Profile entity = profileRepository.save(profile);
        user.addProfile(entity);

        return entity.getId();
    }

    @Transactional
    public void updateProfile(final UUID id, final Profile profile) {
        profileRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + profile.getId(), HttpStatus.NOT_FOUND));
        profile.setId(id);
        profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(UUID id) {
        profileRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + id, HttpStatus.NOT_FOUND));

        profileRepository.deleteById(id);
    }
}
