package com.revshop.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param ex
     * @param model
     * @return
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException ex, Model model){
        model.addAttribute("error", ex.getMessage());

        return "register";
    }

    /**
     * @param ex
     */
    @ExceptionHandler(UserNotFoundException.class)
    public void handleUserNotFoundException(UserNotFoundException ex){
        System.out.println("User not found Exception occured");
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public void handleOrderNotFoundException(OrderNotFoundException ex){
        System.out.println("Order Not Found occured");
    }

    @ExceptionHandler(ReviewRestrictedException.class)
    public void handleReviewRestrictedException(ReviewRestrictedException ex){
        System.out.println("You cannot review one order more than once");
    }





}
