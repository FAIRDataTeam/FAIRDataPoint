package nl.dtls.fairdatapoint.service;

import nl.dtls.fairdatapoint.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UtilityService {
    
    @Value("${instance.behindProxy:true}")
    private Boolean behindProxy;

    public String getRemoteAddr(HttpServletRequest request) {
        return HttpUtil.getClientIpAddress(request, behindProxy);
    }
}
