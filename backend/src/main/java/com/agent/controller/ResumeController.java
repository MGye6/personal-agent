package com.agent.controller;

import com.agent.dto.response.ApiResponse;
import com.agent.entity.Resume;
import com.agent.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/my")
    public ApiResponse<Resume> getMyResume() {
        Resume resume = resumeService.getMyResume();
        return ApiResponse.success(resume);
    }

    @PostMapping
    public ApiResponse<Resume> createResume(@RequestBody Resume resume) {
        Resume created = resumeService.createResume(resume);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Resume> updateResume(@PathVariable Long id, @RequestBody Resume resume) {
        resume.setId(id);
        Resume updated = resumeService.updateResume(resume);
        return ApiResponse.success(updated);
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportResumeToPdf(@PathVariable Long id) {
        Resume resume = resumeService.getMyResume();
        byte[] pdfBytes = resumeService.exportResumeToPdf(resume);
        
        String filename = (resume.getName() != null ? resume.getName() : "resume") + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
