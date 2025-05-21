package com.codeflow.toolflow.scheduler;

import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.persistence.loan.entity.LoanTool;
import com.codeflow.toolflow.persistence.loan.repository.LoanRepository;
import com.codeflow.toolflow.service.email.EmailService;
import com.codeflow.toolflow.service.loan.LoanService;
import com.codeflow.toolflow.util.enums.LoanStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class LoanScheduler {

    private final EmailService emailService;
    private final LoanService loanService;
    private final LoanRepository loanRepository;

    @Value("${email.admin.from}")
    private String adminEmail;

    public LoanScheduler(LoanService loanService, EmailService emailService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.emailService = emailService;
        this.loanRepository = loanRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processDueLoans() {
        LocalDate today = LocalDate.now();

        List<Loan> loans = loanService.getAllLoans();

        loans.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.ON_LOAN && isOverdue(loan.getDueDate(), today))
                .forEach(loan -> handleFirstOverdueNotification(loan, today));

        loans.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.OVERDUE && isOverdue(loan.getDueDate(), today))
                .forEach(loan -> handleOngoingOverdueReminder(loan, today));
    }

    private boolean isOverdue(LocalDate dueDate, LocalDate today) {
        return dueDate != null && dueDate.isBefore(today);
    }

    private void handleFirstOverdueNotification(Loan loan, LocalDate today) {
        String subject = "🔔 EL préstamo #" + loan.getId() + " acaba de vencer";
        String html = buildHtmlEmailBody(loan, loan.getLoanTools(), false, 0);
        loan.setLoanStatus(LoanStatus.OVERDUE);
        loanRepository.save(loan);
        emailService.sendHtmlEmail(adminEmail, subject, html);
    }

    private void handleOngoingOverdueReminder(Loan loan, LocalDate today) {
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);
        String subject = "🔔 EL préstamo #" + loan.getId() + " está vencido";
        String html = buildHtmlEmailBody(loan, loan.getLoanTools(), true, overdueDays);
        emailService.sendHtmlEmail(adminEmail, subject, html);
    }

    private String buildHtmlEmailBody(Loan loan, List<LoanTool> tools, boolean isReminder, long overdueDays) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Arial,sans-serif;'>");

        html.append("<h2 style='color:#d9534f;'>🔔 Préstamo #").append(loan.getId());
        html.append(isReminder ? " está vencido" : " acaba de vencer").append("</h2>");

        html.append("<p><strong>Responsable del préstamo:</strong> ")
                .append(loan.getResponsible() != null ? loan.getResponsible().getName() + " " + loan.getResponsible().getLastName() : "No asignado")
                .append("<br><strong>Docente asignado:</strong> ")
                .append(loan.getTeacher() != null ? loan.getTeacher().getName() + " " + loan.getTeacher().getLastName() : "No asignado")
                .append("<br><strong>Fecha de vencimiento:</strong> ").append(loan.getDueDate())
                .append("</p>");

        if (isReminder) {
            html.append("<p style='color:red;'><strong>Este préstamo lleva atrasado ")
                    .append(overdueDays).append(" día(s)</strong></p>");
        }

        html.append("<h3>📋 Herramientas Prestadas</h3>");
        html.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:100%;'>");
        html.append("<thead><tr style='background:#f2f2f2;'>")
                .append("<th>#</th><th>Herramienta</th><th>Pedida</th><th>Prestada</th><th>Entregada</th><th>Dañada</th><th>Responsable</th>")
                .append("</tr></thead><tbody>");

        int counter = 1;
        for (LoanTool tool : tools) {
            html.append("<tr>")
                    .append("<td>").append(counter++).append("</td>")
                    .append("<td>").append(tool.getTool().getToolName()).append("</td>")
                    .append("<td>").append(tool.getRequested()).append("</td>")
                    .append("<td>").append(tool.getLoaned()).append("</td>")
                    .append("<td>").append(tool.getDelivered()).append("</td>")
                    .append("<td>").append(tool.getDamaged()).append("</td>")
                    .append("<td>").append(tool.getResponsible() != null ? tool.getResponsible().getName() + " " + tool.getResponsible().getLastName() : "No asignado").append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>");
        html.append("<br><p style='font-size:small;color:gray;'>Este es un mensaje automático de ToolFlow. No responder directamente a este correo.</p>");
        html.append("</body></html>");

        return html.toString();
    }
}