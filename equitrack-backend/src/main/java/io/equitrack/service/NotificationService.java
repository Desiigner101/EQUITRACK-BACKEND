package io.equitrack.service;

import io.equitrack.dto.ExpenseDTO;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.ExpenseRepository;
import io.equitrack.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${equitrack.frontend.url}")
    private String frontEndUrl;

    //@Scheduled(cron = "0 * * * * *", zone = "Asia/Manila")
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Manila")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles){
            String body = "Hi! " + profile.getFullName() + ",<br><br>"
                    + "This is i friendly reminder to add your income and expenses for today in Equitrack.<br><br>"
                    + "<a href="+frontEndUrl+" style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>Go to Equitrack</a>"
                    + "<br><br>Best regards,<br>Equitrack Team";
        emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your income and expenses", body);
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

    //@Scheduled(cron = "0 * * * * *", zone = "Asia/Manila")
    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Manila")
    public void sendDailyExpenseSummary(){
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for(ProfileEntity profile: profiles){
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Manila")));

            if(!todaysExpenses.isEmpty()){
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th></tr>");
                int i = 1;

                for(ExpenseDTO expense: todaysExpenses){
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hi! "+profile.getFullName()+",<br/><br/> Here is a summary of your expenses for today:<br/><br/>"+table+"<br/><br/>Best regards,<br/> Equitrack Team";
                emailService.sendEmail(profile.getEmail(), "Your daily Expense summary", body);
            }
            log.info("Job completed: sendDailyExpenseSummary()");
        }
    }

}
