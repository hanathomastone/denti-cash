package com.kaii.dentix.global.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@RequiredArgsConstructor
public class PasswordSerializer extends JsonSerializer<String> {

	private final PasswordEncoder passwordEncoder;

	@Override
	public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(passwordEncoder.encode(s));
	}

	@Override
	public Class<String> handledType() {
		return String.class;
	}
}