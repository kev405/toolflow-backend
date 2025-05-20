package com.codeflow.toolflow.scheduler;

import com.codeflow.toolflow.dto.loan.LoanResponse;
import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.service.loan.impl.LoanServiceImpl;
import com.codeflow.toolflow.util.enums.LoanStatus;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

import com.codeflow.toolflow.service.email.EmailService;
import com.codeflow.toolflow.service.loan.LoanService;

@Component
public class LoanScheduler {

    private final EmailService emailService;
    public LoanService loanService;
    public LoanScheduler(LoanService loanService) {
        this.loanService = loanService;
        this.emailService = new EmailService();
    }

    // Variables simuladas
    private final String correoEncargado = "samuel.galindo@correounivalle.edu.co"; //Cambiar por el correo del encargado

    @Scheduled(cron = "0 0 0  * * *") // Todos los días a las 00:00
    public void procesarVencimientos() {
        LocalDate hoy = LocalDate.now();

        List<LoanResponse> prestamos = loanService.getAllLoans();

        List<LoanResponse> prestamosActivos = prestamos.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.ON_LOAN)
                .toList();

        List<LoanResponse> prestamosAtrasados= prestamos.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.OVERDUE)
                .toList();

        //Logica correo primera vez
        for (LoanResponse prestamo : prestamosActivos) {
            String fechaVencimiento = prestamo.getDueDate();
            LocalDate vencimiento = LocalDate.parse(fechaVencimiento);

            if (vencimiento != null && vencimiento.isBefore(hoy)) {
                System.out.println("Préstamo ID " + prestamo.getId() + " está vencido. Fecha vencimiento: " + fechaVencimiento);
                // Aquí puedes hacer la lógica que quieras, ej. enviar alerta, cambiar estado, etc.
                emailService.sendSimpleEmail(correoEncargado, "Prestamo Vencido",
                        "El prestamo ha vencido el " + fechaVencimiento + ". Por favor, revisa el sistema.");

                prestamo.setLoanStatus(LoanStatus.OVERDUE);
            } else {
                System.out.println("Préstamo ID " + prestamo.getId() + " está vigente.");
            }
        }

        for (LoanResponse prestamo : prestamosAtrasados) {

        }

//        if (!hoy.isBefore(fechaVencimiento)) {
//            System.out.println("📬 Enviando correo al encargado: " + correoEncargado);
//            System.out.println("📅 Fecha vencida: " + fechaVencimiento);
//            emailService.sendSimpleEmail(correoEncargado, "Prestamo Vencido",
//                    "El prestamo ha vencido el " + fechaVencimiento + ". Por favor, revisa el sistema.");
//        } else {
//            System.out.println("✅ Aún no se ha vencido. Fecha de hoy: " + hoy);
//        }
    }

}
