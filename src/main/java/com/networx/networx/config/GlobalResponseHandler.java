package com.networx.networx.config;

import com.networx.networx.dto.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Component
public class GlobalResponseHandler {
    @ResponseBody
    public <T>ResponseEntity<ApiResponse<T>> wrapResponse(T data, String message, boolean success, HttpStatus status){
        ApiResponse<T> response = new ApiResponse<>(success, message, data);
        return new ResponseEntity<>(response, status);
    }
    // Overloaded method without data parameter
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> wrapResponse(String message, boolean success, HttpStatus status) {
        ApiResponse<Void> response = new ApiResponse<>(success, message, null);
        return new ResponseEntity<>(response, status);
    }

    public void wrapResponse(Object data, String message, boolean success, int statusCode, HttpServletResponse response) throws IOException {
        ApiResponse<?> apiResponse = new ApiResponse<>(success, message, data);
        response.setStatus(statusCode);
        response.getWriter().write(apiResponse.toJson()); // Assumes toJson() method exists in ApiResponse
    }
}
