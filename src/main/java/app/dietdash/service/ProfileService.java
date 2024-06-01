package app.dietdash.service;

import app.dietdash.model.Profile;
import app.dietdash.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id).orElse(null);
    }

    @Transactional
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
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
