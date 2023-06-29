package com.kaii.dentix.common;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@Ignore
public class ControllerTest {
}