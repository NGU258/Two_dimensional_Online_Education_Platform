package com.edu.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class Cat implements Serializable {

    private static final long serialVersionUID = 2L ;

    private String name;

    private Integer age;

    private String sex;

    private String other;

    private String carrer;

}
