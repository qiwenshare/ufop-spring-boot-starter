package com.qiwenshare.ufop.config;

import com.qiwenshare.ufop.domain.QiniuyunKodo;
import lombok.Data;

@Data
public class QiniuyunConfig {
    private QiniuyunKodo kodo = new QiniuyunKodo();
}
