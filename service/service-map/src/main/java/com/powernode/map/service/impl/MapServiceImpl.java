package com.powernode.map.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.map.service.MapService;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.map.DrivingLineVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapServiceImpl implements MapService {

    @Resource
    private RestTemplate restTemplate;

    @Value("${tencent.map.key}")
    private String key;

    @Override
    public DrivingLineVo calculateLine(CalculateDrivingLineForm lineForm){
        String url = "dizhi";
        HashMap<String,String> map = new HashMap<>();
        map.put("form",lineForm.getStartPointLongitude() + "," + lineForm.getStartPointLatitude());
        map.put("to",lineForm.getEndPointLongitude() + "," + lineForm.getEndPointLatitude());
        map.put("key",key);

        JSONObject result = restTemplate.getForObject(url, JSONObject.class, map);

        if (result.getIntValue("status") != 0){
            throw new PowerException(ResultCodeEnum.MAP_FAIL);
        }

        JSONObject route = result.getJSONObject("route").getJSONArray("routes").getJSONObject(0);

        DrivingLineVo drivingLineVo = new DrivingLineVo();

        BigDecimal meter = route.getBigDecimal("distance");
        BigDecimal kilometer = meter.divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.HALF_UP);
        drivingLineVo.setDistance(kilometer);

        drivingLineVo.setDuration(route.getBigDecimal("duration"));
        drivingLineVo.setPolyline(route.getJSONArray("polyline"));

        return drivingLineVo;

    }

}
