package hello.upload1.controller;

import hello.upload1.service.VentureStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureStatusController {

    private final VentureStatusService ventureStatusService;

    @PostMapping("/api/ventureStatus")
    public JSONObject getCompanyNum(@RequestBody String b_no) {
        return ventureStatusService.getCompanyNum(b_no);
    }

}
