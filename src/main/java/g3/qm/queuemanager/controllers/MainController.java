package g3.qm.queuemanager.controllers;

import g3.qm.queuemanager.dtos.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/health")
    public ResponseEntity<Health> health() {
        return new ResponseEntity<>(new Health("OK"), HttpStatus.OK);
    }
}
