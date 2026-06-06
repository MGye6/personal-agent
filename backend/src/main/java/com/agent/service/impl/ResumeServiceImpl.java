package com.agent.service.impl;

import com.agent.context.UserContext;
import com.agent.entity.Resume;
import com.agent.mapper.ResumeMapper;
import com.agent.service.ResumeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    @Override
    public Resume getMyResume() {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId)
                .eq(Resume::getDeleted, 0)
                .orderByDesc(Resume::getCreatedAt)
                .last("LIMIT 1");
        Resume resume = resumeMapper.selectOne(wrapper);
        if (resume == null) {
            resume = Resume.builder()
                    .userId(userId)
                    .title("我的简历")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            resumeMapper.insert(resume);
        }
        return resume;
    }

    @Override
    public Resume createResume(Resume resume) {
        Long userId = UserContext.getUserId();
        resume.setUserId(userId);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setUpdatedAt(LocalDateTime.now());
        resumeMapper.insert(resume);
        return resume;
    }

    @Override
    public Resume updateResume(Resume resume) {
        Long userId = UserContext.getUserId();
        resume.setUserId(userId);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeMapper.updateById(resume);
        return resume;
    }

    @Override
    public byte[] exportResumeToPdf(Resume resume) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(bfChinese, 18, Font.BOLD);
            Font sectionFont = new Font(bfChinese, 14, Font.BOLD);
            Font contentFont = new Font(bfChinese, 10, Font.NORMAL);

            if (resume.getName() != null && !resume.getName().isEmpty()) {
                Paragraph title = new Paragraph(resume.getName() + " - " + (resume.getTitle() != null ? resume.getTitle() : "个人简历"), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20f);
                document.add(title);
            } else {
                Paragraph title = new Paragraph(resume.getTitle() != null ? resume.getTitle() : "个人简历", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20f);
                document.add(title);
            }

            PdfPTable contactTable = new PdfPTable(2);
            contactTable.setWidthPercentage(100);
            contactTable.setSpacingBefore(10f);
            contactTable.setSpacingAfter(20f);

            addContactCell(contactTable, "姓名", resume.getName(), contentFont);
            addContactCell(contactTable, "电话", resume.getPhone(), contentFont);
            addContactCell(contactTable, "邮箱", resume.getEmail(), contentFont);
            addContactCell(contactTable, "所在城市", resume.getLocation(), contentFont);

            document.add(contactTable);

            addSection(document, "自我介绍", resume.getSelfIntroduction(), sectionFont, contentFont);
            addSection(document, "教育经历", resume.getEducation(), sectionFont, contentFont);
            addSection(document, "工作经历", resume.getWorkExperience(), sectionFont, contentFont);
            addSection(document, "项目经历", resume.getProjects(), sectionFont, contentFont);
            addSection(document, "技能", resume.getSkills(), sectionFont, contentFont);
            addSection(document, "获奖情况", resume.getAwards(), sectionFont, contentFont);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出PDF失败", e);
        }
    }

    private void addContactCell(PdfPTable table, String label, String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);
        String text = label + ": " + (value != null ? value : "-");
        cell.setPhrase(new Phrase(text, font));
        table.addCell(cell);
    }

    private void addSection(Document document, String title, String content, Font titleFont, Font contentFont) throws DocumentException {
        if (content != null && !content.trim().isEmpty()) {
            Paragraph sectionTitle = new Paragraph(title, titleFont);
            sectionTitle.setSpacingBefore(15f);
            sectionTitle.setSpacingAfter(8f);
            document.add(sectionTitle);

            String[] lines = content.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Paragraph paragraph = new Paragraph(line, contentFont);
                    paragraph.setIndentationLeft(20f);
                    paragraph.setSpacingAfter(3f);
                    document.add(paragraph);
                }
            }
        }
    }
}
