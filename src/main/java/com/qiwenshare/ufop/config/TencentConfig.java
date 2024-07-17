package com.qiwenshare.ufop.config;

import com.qiwenshare.ufop.domain.TencentCOS;
import lombok.Data;

@Data
public class TencentConfig {
    private TencentCOS cos = new TencentCOS();


}
