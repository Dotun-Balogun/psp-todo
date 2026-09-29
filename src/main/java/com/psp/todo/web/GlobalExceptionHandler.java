package com.psp.todo.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    String database(HttpServletResponse res, Model m) {
        res.setStatus(503);
        m.addAttribute("status", 503);
        return "error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    String notFound(HttpServletResponse res, Model m) {
        res.setStatus(404);
        m.addAttribute("status", 404);
        return "error";
    }
}
