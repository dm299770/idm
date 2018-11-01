package com.xxx.demo.services.vehicle;

import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.models.vehicle.TrUserVin;

import java.util.List;

/**
 * 车辆信息管理
 *
 * @author leo
 */
public interface VehicleService {
    /**
     * 查找用户全部绑定车辆
     */
    JSONObject findBindVehicleByUser(String userId);

    /**
     * 绑定车辆
     */
    JSONObject saveVehicle (TrUserVin trUserVin);

    /**
     * 解绑车辆
     */
    JSONObject updateVehicle(String userId ,String plateNum);

    /**
     * 设置默认车辆
     */
    JSONObject setDefaultVehicle(String userId , String plateNum);






}
