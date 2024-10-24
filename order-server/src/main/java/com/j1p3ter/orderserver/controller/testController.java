//package com.j1p3ter.orderserver.controller;
//
//import com.j1p3ter.orderserver.application.service.TossPaymentService;
//import com.j1p3ter.orderserver.domain.payment.Payment;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.thymeleaf.TemplateEngine;
//
//@Controller()
//public class testController {
//
//    private final TossPaymentService tossPaymentService;
//    private final TemplateEngine templateEngine;
//
//    @GetMapping("/test/test")
//    public String failPaymentTest(HttpServletRequest request, Model model) throws Exception {
//        String failCode = request.getParameter("code");
//        String failMessage = request.getParameter("message");
//
//        model.addAttribute("code", failCode);
//        model.addAttribute("message", failMessage);
//
//        return "/fail";
//    }
//
//    @GetMapping("/payments/success")
//    public ResponseEntity<String> paymentSuccess(@RequestParam String paymentKey) {
//        Payment payment = tossPaymentService.paymentSuccess(paymentKey);
//        Context context = new Context();
//        context.setVariable("orderId", payment.getOrderId());
//        context.setVariable("orderName", payment.getOrderName());
//        context.setVariable("amount", payment.getAmount());
//        context.setVariable("createdAt", payment.getCreatedAt());
//
//        String htmlContent = templateEngine.process("success", context);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_HTML)
//                .body(htmlContent);
//    }
//
//    @GetMapping("/payments/fail")
//    public void paymentFail(@RequestParam String paymentKey) {
//        paymentService.paymentFail(paymentKey);
//    }
//}
