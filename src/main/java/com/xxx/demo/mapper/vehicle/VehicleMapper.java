package com.xxx.demo.mapper.vehicle;

import com.xxx.demo.dto.sys.UserInfo;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.models.vehicle.TrUserVin;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 绑定车辆信息管理
 *
 * @author leo.
 */
public interface VehicleMapper {
    /**
     * 根据userId查找所有绑定车辆
     * @param userId 用户主键
     * @return
     */
     List<TrUserVin> findById(@Param("userId") String userId);


    /**
     * 根据userId绑定车辆
     * @param trUserVin 车辆信息
     * @return
     */
    //void saveTrUserVin(@Param("userId") String userId,@Param("plateNum") String plateNum,@Param("lastSixPhoneNum") String lastSixPhoneNum,@Param("engineNum")String engineNum,@Param("vin")String vin);
    void saveTrUserVin(TrUserVin trUserVin);

    /***
     * 根据userId解绑车辆
     * @param userId
     * @param plateNum
     */
    void unbindTrUserVin(@Param("userId")String userId , @Param("plateNum") String plateNum);

    /**
     * 根据车牌号设置默认车辆
     * @param userId
     * @param plateNum
     * @param defaultVehicle
     */
    void setDefaultVehicle(@Param("userId")String userId,@Param("plateNum")String plateNum,@Param("defaultVehicle")Integer defaultVehicle );

    /**
     * 去除当前用户下所有默认车辆
     * @param userId
     */
    void clearDefaultVehicle(@Param("userId")String userId);

    /**
     * 根据车牌号获取车辆信息
     * @param plateNum
     */
    TrUserVin findVehicleByPlateNum(@Param("plateNum")String plateNum);


}
