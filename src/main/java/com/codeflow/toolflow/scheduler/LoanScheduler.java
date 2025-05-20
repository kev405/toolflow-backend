package com.codeflow.toolflow.scheduler;

import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.persistence.loan.entity.LoanTool;
import com.codeflow.toolflow.persistence.loan.repository.LoanRepository;
import com.codeflow.toolflow.util.enums.LoanStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.codeflow.toolflow.service.email.EmailService;
import com.codeflow.toolflow.service.loan.LoanService;

@Component
public class LoanScheduler {

    private final EmailService emailService;
    public LoanService loanService;
    private final LoanRepository loanRepository;

    public LoanScheduler(LoanService loanService, EmailService emailService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.emailService = emailService;
        this.loanRepository = loanRepository;
    }

    // Variables simuladas
    private final String correoEncargado = "samuel.galindo@correounivalle.edu.co"; //Cambiar por el correo del encargado

    @Scheduled(cron = "0 * * * * *")
    public void procesarVencimientos() {
        LocalDate today = LocalDate.now();

        List<Loan> loans = loanService.getAllLoans();

        List<Loan> activeLoans = loans.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.ON_LOAN)
                .toList();

        List<Loan> onDueLoans = loans.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.OVERDUE)
                .toList();

        //Logica correo primera vez
        for (Loan loan : activeLoans) {
            LocalDate dueDate = loan.getDueDate();
            if (dueDate != null && dueDate.isBefore(today)) {
                List<LoanTool> loanTools = loan.getLoanTools();

                String head = "🔔 EL préstamo #" + loan.getId() + " acaba de vencer";

                StringBuilder body = new StringBuilder();
                body.append("📋 RESUMEN DE HERRAMIENTAS PRESTADAS\n");
                body.append("========================================\n");

                int counter = 1;
                for (LoanTool loanTool : loanTools) {
                    String toolName = loanTool.getTool().getToolName();
                    int quantityRequested = loanTool.getRequested();
                    int quantityLoan = loanTool.getLoaned();
                    int quantityDelivered = loanTool.getDelivered();
                    int quantityDamaged = loanTool.getDamaged();
                    String responsible = "No asignado";

                    if(loanTool.getResponsible() != null)
                    {
                        responsible = loanTool.getResponsible().getName() + " " + loanTool.getResponsible().getLastName();
                    }

                    body.append(String.format(
                            "🔧 Herramienta #%d: %s\n" +
                                    "   • Cantidad Pedida:     %d\n" +
                                    "   • Cantidad Prestada:   %d\n" +
                                    "   • Cantidad Entregada:  %d\n" +
                                    "   • Cantidad Dañada:     %d\n" +
                                    "   • Responsable:     %s\n",
                            counter++, toolName,
                            quantityRequested, quantityLoan, quantityDelivered,
                            quantityDamaged, responsible
                    ));

                    body.append("--------------------------------------------------\n");
                }

                String reponsibleText = "Responsable del prestamo: " + loan.getResponsible().getName() + " " +
                        loan.getResponsible().getLastName() + " Docente asignado: " + loan.getTeacher().getName() + " " + loan.getTeacher().getLastName();

                emailService.sendSimpleEmail(correoEncargado, head,
                        reponsibleText + "\n" + body + "\n" + loan.getDueDate());

                loan.setLoanStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
        }

        //Logica correo reincidente
        for (Loan loan : onDueLoans) {
            LocalDate dueDate = loan.getDueDate();
            if (dueDate != null && dueDate.isBefore(today)) {
                List<LoanTool> loanTools = loan.getLoanTools();

                long dueDays = ChronoUnit.DAYS.between(dueDate, today);
                String head = "🔔 EL préstamo #" + loan.getId() + " está vencido";

                StringBuilder body = new StringBuilder();
                body.append("📋 RESUMEN DE HERRAMIENTAS PRESTADAS\n");
                body.append("========================================\n");

                int counter = 1;
                for (LoanTool loanTool : loanTools) {
                    String toolName = loanTool.getTool().getToolName();
                    int loanToolRequested = loanTool.getRequested();
                    int loanToolLoaned = loanTool.getLoaned();
                    int loanToolDelivered = loanTool.getDelivered();
                    int loanToolDamaged = loanTool.getDamaged();
                    String responsible = "No asignado";

                    if(loanTool.getResponsible() != null)
                    {
                        responsible = loanTool.getResponsible().getName() + " " + loanTool.getResponsible().getLastName();
                    }

                    body.append(String.format(
                            "🔧 Herramienta #%d: %s\n" +
                                    "   • Cantidad Pedida:     %d\n" +
                                    "   • Cantidad Prestada:   %d\n" +
                                    "   • Cantidad Entregada:  %d\n" +
                                    "   • Cantidad Dañada:     %d\n" +
                                    "   • Responsable:     %s\n",
                            counter++, toolName,
                            loanToolRequested, loanToolLoaned, loanToolDelivered,
                            loanToolDamaged, responsible
                    ));

                    body.append("--------------------------------------------------\n");
                }

                String reponsibleText = "Responsable del prestamo: " + loan.getResponsible().getName() + " " +
                        loan.getResponsible().getLastName() + " Docente asignado: " + loan.getTeacher().getName() +
                        " " +loan.getTeacher().getLastName();

                emailService.sendSimpleEmail(correoEncargado, head,
                        reponsibleText + "\n" + body + "\n" + "Fecha de vencimiento: " + loan.getDueDate() + "\n" +
                                "Prestamo atrasado " + dueDays + " dias"
                );
            }
        }
    }
}


