package ru.seims.application.servlet;

import ru.seims.utils.json.JSONReader;
import ru.seims.utils.logging.Logger;
import ru.seims.localization.LocalizationManager;
import ru.seims.MailService.MNSAuthenticator;
import ru.seims.MailService.MessageBean;
import ru.seims.MailService.MessageTemplateBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class MailServiceServlet {
    private final String MNSErrorMessage = "Mail notification service is unavailable. Check connection properties or try restart";
    private final String serviceURL = MNSAuthenticator.loadServiceRemoteURL();

    @GetMapping("/mail")
    public String doGetDef() {
        return "redirect:/mail/templates/apply/0";
    }

    @GetMapping(value = "/mail/recipients/{recipients}")
    public String doGet(@PathVariable String[] recipients,  RedirectAttributes attributes) {
        try {
            ResponseEntity<String> responseEntity = new RestTemplate().exchange(serviceURL + "/status",
                    HttpMethod.GET, new HttpEntity<String>(MNSAuthenticator.getHeaders()), String.class);
            if (!JSONReader.getArgumentValue("status", responseEntity.getBody()).equals("OK")) {
                ServletUtils.showPopup(attributes, MNSErrorMessage, "error");
                return "redirect:/";
            }
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, MNSErrorMessage + "\n" + e.getMessage(), "error");
            return "redirect:/";
        }
        if(recipients != null && recipients.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String recipient : recipients) {
                if(!recipient.isEmpty() && !recipient.toLowerCase(Locale.ROOT).equals("null"))
                    builder.append(recipient).append(";");
            }
            attributes.addAttribute("recipients", builder.toString());
        }
        return "redirect:/mail/templates/apply/0";
    }

    @GetMapping(value = {"/mail/templates", "/mail/templates/get"})
    public String getTemplatesDef() {
        return "redirect:/mail/templates/get/0";
    }

    @GetMapping("/mail/templates/get/{id}")
    public String getTemplates(@PathVariable String id, Model model) {
        ResponseEntity<MessageTemplateBean[]> responseEntity = new RestTemplate()
                .exchange(serviceURL + "/data/templates",
                HttpMethod.GET, new HttpEntity<MessageTemplateBean>(MNSAuthenticator.getHeaders()),
                MessageTemplateBean[].class);
        model.addAttribute("templates", responseEntity.getBody());
        if(id == null || id.isEmpty())
            id = "0";
        model.addAttribute("currentTemplate", id);
        return "views/mailTemplatesView";
    }

    @GetMapping("/mail/templates/apply/{id}")
    public String applyTemplate(@PathVariable String id, RedirectAttributes attributes, Model model, HttpServletRequest request) {
        if(id == null || id.isEmpty()) {
            ServletUtils.showPopup(attributes, "ID is null", "error");
            return "redirect:/mail/templates";
        }
        String rec =  request.getParameter("recipients");
        model.addAttribute("recipients",rec);
        try {
            ResponseEntity<MessageTemplateBean> responseEntity = new RestTemplate().exchange(serviceURL + "/data/templates/get/" + id,
                    HttpMethod.GET, new HttpEntity<String>(MNSAuthenticator.getHeaders()), MessageTemplateBean.class);
            model.addAttribute("appliedTemplate", responseEntity.getBody());
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getMessage(), "error");
        }
        try {
            ResponseEntity<MessageTemplateBean[]> responseEntityList = new RestTemplate()
                    .exchange(serviceURL + "/data/templates",
                            HttpMethod.GET, new HttpEntity<MessageTemplateBean>(MNSAuthenticator.getHeaders()),
                            MessageTemplateBean[].class);
            model.addAttribute("templates", responseEntityList.getBody());
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getMessage(), "error");
        }
        return "views/mailService";
    }

    @PostMapping("/mail/send")
    public String doPost(HttpServletRequest request, Model model, RedirectAttributes attributes) {
        String[] recipients = request.getParameter("recipients").split("[,;\\s\\n]");
        String content = request.getParameter("message");
        String subject = request.getParameter("subject");
        if(content.isEmpty() || subject.isEmpty() || recipients.length == 0) {
            ServletUtils.showPopup(model, LocalizationManager.getString("MSS.empty_err"), "warning");
            return "views/mailService";
        }
        MessageBean message = new MessageBean(subject, content, recipients);
        Logger.log(this, "Trying to send message: " + message.toString(), 1);
        try {
            ResponseEntity<String> responseEntity = new RestTemplate().exchange(serviceURL + "/mail/send",
                    HttpMethod.POST, new HttpEntity<MessageBean>(message, MNSAuthenticator.getHeaders()), String.class);
            Logger.log(this, responseEntity.getBody(), 4);
            if(JSONReader.getArgumentValue("status", responseEntity.getBody()).equals("error")) {
                Logger.log(this, "Couldn't send message.", 2);
                ServletUtils.showPopup(attributes, JSONReader.getArgumentValue("message",
                        responseEntity.getBody()), "error");
            } else Logger.log(this, "Message sent successfully", 1);
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
            ServletUtils.showPopup(attributes, e.getMessage(), "error");
        }
        ServletUtils.showPopup(attributes, "Sent successfully", "warning");
        return "redirect:/mail";
    }

    @PostMapping("/mail/templates/save/{id}")
    public String saveTemplate(@PathVariable String id, HttpServletRequest request, RedirectAttributes attributes) {
        if(id == null || id.isEmpty()) {
            ServletUtils.showPopup(attributes, "ID is null", "error");
            return "redirect:/mail/templates";
        }
        String subject = request.getParameter("subject");
        String content = request.getParameter("message");
        String signature = request.getParameter("signature");
        MessageTemplateBean template = new MessageTemplateBean(Integer.parseInt(id), subject, content, signature);
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(serviceURL + "/data/templates/update/"+id,
                HttpMethod.PUT, new HttpEntity<MessageTemplateBean>(template, MNSAuthenticator.getHeaders()), String.class);
        if(JSONReader.getArgumentValue(responseEntity.getBody(), "status").equals("OK")) {
            ServletUtils.showPopup(attributes, "Saved", "warning");
            return "redirect:/mail/templates/"+JSONReader.getArgumentValue(responseEntity.getBody(), "id");
        } else {
            ServletUtils.showPopup(attributes, "Error: " + JSONReader.getArgumentValue(responseEntity.getBody(),
                    "message"), "error");
            return "redirect:/mail/templates/get/"+id;
        }
    }

    @PostMapping("/mail/templates/new")
    public String addTemplate(RedirectAttributes attributes) {
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(serviceURL + "/data/templates/add",
                HttpMethod.POST, new HttpEntity<String>(MNSAuthenticator.getHeaders()), String.class);
        if(JSONReader.getArgumentValue(responseEntity.getBody(), "status").equals("OK")) {
            ServletUtils.showPopup(attributes, "Added", "warning");
            return "redirect:/mail/templates/"+JSONReader.getArgumentValue(responseEntity.getBody(), "id");
        } else {
            ServletUtils.showPopup(attributes, "Error: " + JSONReader.getArgumentValue(responseEntity.getBody(),
                    "message"), "error");
            return "redirect:/mail/templates";
        }
    }

    @PostMapping("/mail/templates/delete/{id}")
    public String deleteTemplate(@PathVariable String id,  RedirectAttributes attributes) {
        if(id == null || id.isEmpty()) {
            ServletUtils.showPopup(attributes, "ID is null", "error");
            return "redirect:/mail/templates";
        }
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(serviceURL + "/data/templates/delete/"+id,
                HttpMethod.DELETE, new HttpEntity<String>(MNSAuthenticator.getHeaders()), String.class);
        if(JSONReader.getArgumentValue(responseEntity.getBody(), "status").equals("OK")) {
            ServletUtils.showPopup(attributes, "Deleted", "warning");
            return "redirect:/mail/templates";
        } else {
            ServletUtils.showPopup(attributes, "Error: " + JSONReader.getArgumentValue(responseEntity.getBody(),
                    "message"), "error");
            return "redirect:/mail/templates";
        }
    }

    @PutMapping("/mail/updateCredentials/{user}/{pass}")
    public String doPutCreds(@PathVariable String user, @PathVariable String pass, RedirectAttributes attributes) {
        sendUpdateCredsRequest(user, pass, attributes);
        return "redirect:/mail";
    }

    @PutMapping("/mail/updateAccount/{login}/{pass}")
    public String doPutAccount(@PathVariable String login, @PathVariable String pass, RedirectAttributes attributes) {
        sendUpdateAccRequest(login, pass, attributes);
        return "redirect:/mail";
    }

    @PostMapping("/mail/updateCredentials")
    public String doPutCreds(HttpServletRequest request, RedirectAttributes attributes) {
        String user = request.getParameter("new_user");
        String pass = request.getParameter("new_pass");
        sendUpdateCredsRequest(user, pass, attributes);
        return "redirect:/mail";
    }

    @PostMapping("/mail/updateAccount")
    public String doPutAccount(HttpServletRequest request, RedirectAttributes attributes) {
        String login = request.getParameter("new_login");
        String pass = request.getParameter("new_pass");
        sendUpdateAccRequest(login, pass, attributes);
        return "redirect:/mail";
    }

    private void sendUpdateCredsRequest(String user, String pass, RedirectAttributes attributes) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", user);
        params.put("pass", pass);
        ResponseEntity<String> responseEntity =
                new RestTemplate().exchange(serviceURL + "/credentials/update/{user}/{pass}",
                        HttpMethod.PUT, new HttpEntity<String>(MNSAuthenticator.getHeaders()), String.class, params);
        if (!JSONReader.getArgumentValue("status", responseEntity.getBody()).equals("OK")) {
            ServletUtils.showPopup(attributes,
                    JSONReader.getArgumentValue("message", responseEntity.getBody()), "error");
        } else {
            ServletUtils.showPopup(attributes, "Updated", "warning");
            MNSAuthenticator.loadProvidedUserCredentials();
        }
    }

    private void sendUpdateAccRequest(String login, String pass, RedirectAttributes attributes) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", login);
        params.put("pass", pass);
        ResponseEntity<String> responseEntity =
                new RestTemplate().exchange(serviceURL + "/account/update/{login}/{pass}",
                        HttpMethod.PUT, new HttpEntity<String>(MNSAuthenticator.getHeaders()), String.class, params);
        if (!JSONReader.getArgumentValue("status", responseEntity.getBody()).equals("OK")) {
            ServletUtils.showPopup(attributes,
                    JSONReader.getArgumentValue("message", responseEntity.getBody()), "error");
        } else ServletUtils.showPopup(attributes, "Updated", "warning");
    }
}
