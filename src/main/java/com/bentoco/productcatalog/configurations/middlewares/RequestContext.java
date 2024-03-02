package com.bentoco.productcatalog.configurations.middlewares;

import com.bentoco.productcatalog.security.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class RequestContext {
    Profile profile;
}
