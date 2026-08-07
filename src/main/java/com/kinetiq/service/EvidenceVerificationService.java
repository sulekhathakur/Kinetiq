package com.kinetiq.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EvidenceVerificationService {

    private static final Pattern GITHUB_COMMIT_PATTERN =
            Pattern.compile("github\\.com/([^/]+)/([^/]+)/commit/([a-fA-F0-9]+)");

    private final RestTemplate restTemplate;

    public EvidenceVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyGitHubCommit(String url, LocalDate checkinDate) {
        Matcher matcher = GITHUB_COMMIT_PATTERN.matcher(url);

        if (!matcher.find()) {
            return false;
        }

        String owner = matcher.group(1);
        String repo = matcher.group(2);
        String sha = matcher.group(3);

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/commits/%s", owner, repo, sha);

        try {
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null) {
                return false;
            }

            Map<String, Object> commit = (Map<String, Object>) response.get("commit");
            Map<String, Object> author = (Map<String, Object>) commit.get("author");
            String dateString = (String) author.get("date");

            OffsetDateTime commitDateTime = OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
            LocalDate commitDate = commitDateTime.toLocalDate();

            return commitDate.equals(checkinDate);

        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}