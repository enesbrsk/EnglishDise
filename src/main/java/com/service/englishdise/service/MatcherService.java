package com.service.englishdise.service;

import com.service.englishdise.dto.UserDto;
import com.service.englishdise.dto.response.ProfileResponse;
import com.service.englishdise.enums.MatchStatus;
import com.service.englishdise.model.Matcher;
import com.service.englishdise.repository.MatcherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatcherService {

    private final MatcherRepository matcherRepository;
    private final UserService userService;
    private final ProfileService profileService;

    public MatcherService(MatcherRepository matcherRepository, UserService userService, ProfileService profileService
                           ) {
        this.matcherRepository = matcherRepository;
        this.userService = userService;
        this.profileService = profileService;
    }

    public List<ProfileResponse> createMatch(Long matchedUserId){
        UserDto userDto = userService.findUserInContext();
        final List<Matcher> pendingMatches = matcherRepository.findByMatchedUserIdAndMatchStatus(userDto.getId(),MatchStatus.PENDING);

        if (!pendingMatches.isEmpty()){
            for (Matcher match : pendingMatches){
                if (match.getUserId().equals(matchedUserId)){
                    match.setMatchStatus(MatchStatus.ACCEPTED);
                    matcherRepository.save(match);
                    this.acceptedMatch(userDto.getId(),matchedUserId);

                    return profileService.getProfileByUserId(matchedUserId);
                }
            }
        }
            Matcher matcher = Matcher.builder()
                    .userId(userDto.getId())
                    .matchedUserId(matchedUserId)
                    .matchStatus(MatchStatus.PENDING)
                    .build();
            matcherRepository.save(matcher);
            return null;
    }

    public List<ProfileResponse> getFindMatch(){
        UserDto userDto = userService.findUserInContext();

        List<ProfileResponse> profileResponseList = profileService.getAllProfileNotUserId();
        List<Matcher> acceptedMatches = matcherRepository.findByUserIdAndMatchStatus(userDto.getId(),MatchStatus.ACCEPTED);

        List<Long> matcherIds = acceptedMatches.stream()
                .map(Matcher::getMatchedUserId)
                .toList();

        return profileResponseList.stream()
                .filter(profile -> !matcherIds.contains(profile.getUserId()))
                .toList();
    }

    public List<ProfileResponse> getAcceptedMatch(){

        UserDto userDto = userService.findUserInContext();
        List<Matcher> acceptedMatches = matcherRepository.findByUserIdAndMatchStatus(userDto.getId(),MatchStatus.ACCEPTED);
        List<Long> matcherIds = acceptedMatches.stream()
                .map(Matcher::getMatchedUserId)
                .toList();

        return profileService.getAllProfileById(matcherIds);
    }

    private void acceptedMatch(Long userId,Long matchedUserId){
        Matcher matcher = Matcher.builder()
                .userId(userId)
                .matchedUserId(matchedUserId)
                .matchStatus(MatchStatus.ACCEPTED)
                .build();
        matcherRepository.save(matcher);
    }
    
}
