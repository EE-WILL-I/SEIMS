package ru.seims.application.servlet;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ServletContext {
    public static void showPopup(Model model, String message, String type) {
        model.addAttribute("show_popup", type);
        model.addAttribute("popup_message", message);
    }

    public static void showPopup(RedirectAttributes attributes, String message, String type) {
        attributes.addFlashAttribute("show_popup", type);
        attributes.addFlashAttribute("popup_message", message);
    }
}
