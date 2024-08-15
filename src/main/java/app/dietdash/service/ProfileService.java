package app.dietdash.service;

import app.dietdash.model.Profile;
import app.dietdash.model.User;
import app.dietdash.repository.ProfileRepository;
import app.dietdash.repository.UserRepository;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UtilService utilService;

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id).orElse(null);
    }

    @Transactional
    public UUID addProfile(final Profile profile, final Principal principal) {
        final String login = utilService.getLoginFromPrincipal(principal);

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        Profile entity = profileRepository.save(profile);
        user.getProfileList().add(entity);

        return entity.getId();
    }

    @Transactional
    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(UUID id) {
        profileRepository.deleteById(id);
    }

    public List<Profile> getProfilesByName(String term) {
        return profileRepository.findByName(term);
    }
}
