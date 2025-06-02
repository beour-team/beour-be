package com.beour.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private int code;
    private HttpStatus httpStatus;
    private T data;


    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data){
        return new ApiResponse<>(httpStatus.value(), httpStatus, data);
    }

    public static <T> ApiResponse<T> ok(T data){
        return of(HttpStatus.OK, data);
    }

}
