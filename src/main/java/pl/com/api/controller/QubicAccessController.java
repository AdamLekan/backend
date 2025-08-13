package pl.com.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.com.api.dto.AccessRequest;
import pl.com.api.service.QubicAccessService2;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
public class QubicAccessController {

    @Autowired
    private QubicAccessService2 accessService;

    @PostMapping("/grant")
    public String grant(@RequestBody AccessRequest request) throws Exception {
        System.out.println(request.privateKeySeedBase64);
        byte[] privateKeySeed = Base64.getDecoder().decode(request.privateKeySeedBase64);
        return accessService.grantAccess(privateKeySeed, request.fromAddress, request.toAddress, request.dataKey, request.dataValue);
    }

    @PostMapping("/revoke")
    public String revoke(@RequestBody AccessRequest request) throws Exception {
        byte[] seed = Base64.getDecoder().decode(request.privateKeySeedBase64);
        return accessService.revokeAccess(seed, request.fromAddress, request.toAddress, request.dataKey);
    }

    @GetMapping("/check")
    public boolean hasAccess(@RequestParam String from, @RequestParam String to, @RequestParam String dataKey) throws Exception {
        return accessService.hasGrantAccess(from, to, dataKey);
    }

    @GetMapping("/history")
    public List<Map<String, Object>> history(@RequestParam String from, @RequestParam String to, @RequestParam String dataKey) throws Exception {
        return accessService.getGrantHistory(from, to, dataKey);
    }
}