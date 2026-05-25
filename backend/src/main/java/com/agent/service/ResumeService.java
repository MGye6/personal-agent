package com.agent.service;

import com.agent.entity.Resume;

public interface ResumeService {
    Resume getMyResume();
    Resume createResume(Resume resume);
    Resume updateResume(Resume resume);
    byte[] exportResumeToPdf(Resume resume);
}
