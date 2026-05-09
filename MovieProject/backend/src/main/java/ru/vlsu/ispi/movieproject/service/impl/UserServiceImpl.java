package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.exception.AlreadyFollowedException;
import ru.vlsu.ispi.movieproject.exception.FollowNotFoundException;
import ru.vlsu.ispi.movieproject.exception.SelfFollowException;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.UserMapper;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.model.UserFollow;
import ru.vlsu.ispi.movieproject.repository.UserFollowRepository;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.FileStorageService;
import ru.vlsu.ispi.movieproject.service.UserService;
import ru.vlsu.ispi.movieproject.util.AccessUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;
    private final UserFollowRepository userFollowRepository;
    private final EntityManager entityManager;
    private final AccessUtil accessUtil;

    @Override
    public Page<UserDto> searchUsers(String query, String sortOrder, int page, int size) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "username"));

        return userRepository.search(query, pageable).map(userMapper::mapToDto);
    }

    @Override
    public UserDto getCurrentUser() {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException());

        if (user.getDeleted()) throw new UserNotFoundException();

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException());

        if (user.getDeleted()) throw new UserNotFoundException();

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto updateAvatar(MultipartFile file) {
        Long userId = currentUserService.getCurrentUserID();

        return updateAvatarInternal(userId, file);
    }

    @Override
    public UserDto updateAvatarByUserId(Long userId, MultipartFile file) {
        accessUtil.checkAdmin();

        return updateAvatarInternal(userId, file);
    }

    @Override
    public UserDto updateProfile(EditProfileRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        return updateProfileInternal(userId, request);
    }

    @Override
    public UserDto updateProfileByUserId(Long userId, EditProfileRequest request) {
        accessUtil.checkAdmin();

        return updateProfileInternal(userId, request);
    }

    @Override
    public void deleteProfile() {
        Long userId = currentUserService.getCurrentUserID();

        userRepository.deleteById(userId);
    }

    @Override
    public void deleteUserById(Long id) {
        accessUtil.checkAdmin();

        userRepository.deleteById(id);
    }

    @Override
    public void follow(Long followedId) {
        Long userId = currentUserService.getCurrentUserID();

        if (userId.equals(followedId)) {
            throw new SelfFollowException();
        }
        if (userFollowRepository.existsActiveFollow(userId, followedId)) {
            throw new AlreadyFollowedException();
        }

        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(entityManager.getReference(User.class, userId));
        userFollow.setFollowedUser(entityManager.getReference(User.class, followedId));
        userFollow.setFollowedAt(LocalDateTime.now());

        userFollowRepository.save(userFollow);
    }

    @Override
    public void unfollow(Long followedId) {
        Long userId = currentUserService.getCurrentUserID();

        UserFollow userFollow = userFollowRepository.findActiveFollow(userId, followedId)
                .orElseThrow(() -> new FollowNotFoundException());

        userFollowRepository.delete(userFollow);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers() {
        Long userId = currentUserService.getCurrentUserID();

        return userFollowRepository.findFollowers(userId).stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowings() {
        Long userId = currentUserService.getCurrentUserID();

        return userFollowRepository.findFollowings(userId).stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowersByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException());

        return userFollowRepository.findFollowers(user.getId()).stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowingsByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException());

        return userFollowRepository.findFollowings(user.getId()).stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    private UserDto updateProfileInternal(Long userId, EditProfileRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException());

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        user.setAboutMe(request.getAboutMe());

        return userMapper.mapToDto(user);
    }

    private UserDto updateAvatarInternal(Long userId, MultipartFile file) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException());

        String avatarUrl = fileStorageService.upload(file, FileDirectory.AVATARS.getFolder());
        fileStorageService.delete(user.getAvatarUrl());
        user.setAvatarUrl(avatarUrl);

        return userMapper.mapToDto(user);
    }
}
